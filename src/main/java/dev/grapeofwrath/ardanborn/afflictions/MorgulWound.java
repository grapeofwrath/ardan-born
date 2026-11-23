package dev.grapeofwrath.ardanborn.afflictions;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.items.rings.RingBearer;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.weapons.Miruvor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/// The Morgul wound: a cold blade's wound from the servants of the Shadow, which worsens in stages until
/// athelas or miruvor treats it. A Healer can hold it back for a time.
///
/// Only undead can wound. With LevelledMobs installed, the mob's level (which LevelledMobs keeps in the
/// mob's persistent data as `levelledmobs:level`) must reach `min-level`; there's no code dependency on
/// LevelledMobs. Without it, only wither skeletons and the servants of a betraying Ring of Power can wound.
///
/// The wound's stage and the time left until it worsens are kept on the player. The time only runs while
/// they're online.
public final class MorgulWound implements Listener {
    private static final NamespacedKey STAGE = Keys.key("morgul_stage");
    private static final NamespacedKey LEFT = Keys.key("morgul_left_ms");
    private static final NamespacedKey HELD_UNTIL = Keys.key("morgul_held_until");
    private static final long HOLD_COOLDOWN_MS = 10 * 60_000;
    private static final double DEATH_EASE_CHANCE = 0.25;
    private static final NamespacedKey LEVELLEDMOBS_LEVEL = new NamespacedKey("levelledmobs", "level");
    private static final int MAX_STAGE = 4;
    private static final List<String> STAGE_NAMES = List.of("",
            "A cold wound: the blade's chill is in you.",
            "The chill spreads.",
            "The shadow takes hold.",
            "You are fading into the shadow!");

    private static boolean enabled = true;
    private static double chance = 0.10;
    private static double servantChance = 0.50;
    private static int minLevel = 30;
    private static long stageMs = 180_000;

    public static void register(Plugin plugin) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("morgul-wound");
        if (config != null) {
            enabled = config.getBoolean("enabled", true);
            chance = config.getDouble("chance", 0.10);
            servantChance = config.getDouble("servant-chance", 0.50);
            minLevel = config.getInt("min-level", 30);
            stageMs = Math.max(1, config.getLong("stage-minutes", 3)) * 60_000;
        }
        MorgulWound wound = new MorgulWound();
        Bukkit.getPluginManager().registerEvents(wound, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, wound::tickAll, 20L, 20L);
        Miruvor.onDrink(MorgulWound::ease);
    }

    // ------------------------------------------------------------------ the wound's state

    public static int stage(Player player) {
        return player.getPersistentDataContainer().getOrDefault(STAGE, PersistentDataType.INTEGER, 0);
    }

    public static boolean isWounded(Player player) {
        return stage(player) > 0;
    }

    private static void setStage(Player player, int stage) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (stage <= 0) {
            pdc.remove(STAGE);
            pdc.remove(LEFT);
            return;
        }
        pdc.set(STAGE, PersistentDataType.INTEGER, stage);
        pdc.set(LEFT, PersistentDataType.LONG, stageMs);
        player.sendActionBar(Component.text(STAGE_NAMES.get(stage), NamedTextColor.DARK_AQUA)
                .decoration(TextDecoration.ITALIC, true));
    }

    /** Heals the wound completely (a Healer's hands, athelas). True if there was one. */
    public static boolean cure(Player player) {
        if (!isWounded(player)) return false;
        setStage(player, 0);
        player.removePotionEffect(PotionEffectType.WITHER);
        player.removePotionEffect(PotionEffectType.DARKNESS);
        player.sendActionBar(Component.text("The cold leaves you.", NamedTextColor.AQUA));
        return true;
    }

    /// A Healer's hands: the wound's timer starts over, but the wound stays. Each wounded player can be held
    /// back once every [#HOLD_COOLDOWN_MS], whoever the Healer is. Returns false if it's too soon (or there's
    /// no wound).
    public static boolean holdBack(Player player) {
        if (!isWounded(player)) return false;
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        long now = System.currentTimeMillis();
        if (pdc.getOrDefault(HELD_UNTIL, PersistentDataType.LONG, 0L) > now) return false;
        pdc.set(HELD_UNTIL, PersistentDataType.LONG, now + HOLD_COOLDOWN_MS);
        pdc.set(LEFT, PersistentDataType.LONG, stageMs);
        player.sendActionBar(Component.text("Healing hands hold the cold at bay.", NamedTextColor.AQUA));
        return true;
    }

    /** Miruvor: one stage back, with the timer restarted. A first-stage wound is cured. */
    public static void ease(Player player) {
        int stage = stage(player);
        if (stage <= 0) return;
        if (stage == 1) {
            cure(player);
        } else {
            setStage(player, stage - 1);
        }
    }

    // ------------------------------------------------------------------ taking the wound

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!enabled || !(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (!(event.getDamager() instanceof LivingEntity attacker) || !canWound(attacker)) return;
        double odds = RingBearer.isServant(attacker) ? servantChance : chance;
        if (isWounded(player) || ThreadLocalRandom.current().nextDouble() >= odds) return;
        setStage(player, 1);
    }

    private static boolean canWound(LivingEntity mob) {
        if (RingBearer.isServant(mob)) return true;
        if (!OriginAbilities.UNDEAD.contains(mob.getType())) return false;
        if (Bukkit.getPluginManager().isPluginEnabled("LevelledMobs")) {
            Integer level = mob.getPersistentDataContainer().get(LEVELLEDMOBS_LEVEL, PersistentDataType.INTEGER);
            return level != null && level >= minLevel;
        }
        return mob instanceof WitherSkeleton;
    }

    // ------------------------------------------------------------------ every second

    private void tickAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int stage = stage(player);
            if (stage > 0) tick(player, stage);
        }
    }

    private static void tick(Player player, int stage) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        long left = pdc.getOrDefault(LEFT, PersistentDataType.LONG, stageMs) - 1000;
        if (left <= 0 && stage < MAX_STAGE) {
            setStage(player, ++stage);
        } else {
            pdc.set(LEFT, PersistentDataType.LONG, Math.max(0, left));
        }

        effect(player, PotionEffectType.SLOWNESS, 0);
        if (stage >= 2) {
            effect(player, PotionEffectType.WEAKNESS, 0);
            effect(player, PotionEffectType.MINING_FATIGUE, 0);
        }
        if (stage == 3) {
            if (ThreadLocalRandom.current().nextDouble() < 0.25) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 0, true, false, true));
            }
            effect(player, PotionEffectType.DARKNESS, 0);
        }
        if (stage >= MAX_STAGE) {
            effect(player, PotionEffectType.WITHER, 1);
            effect(player, PotionEffectType.DARKNESS, 0);
        }
        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0, 1, 0), 3, 0.2, 0.4, 0.2, 0.005);
    }

    private static void effect(Player player, PotionEffectType type, int amplifier) {
        player.addPotionEffect(new PotionEffect(type, 50, amplifier, true, false, true));
    }

    /// Death doesn't cure the wound, so nobody dies on purpose to be rid of it. It may ease a stage (never
    /// to nothing), and a fading wound always eases, or Wither II would kill the player again on respawn.
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        int stage = stage(player);
        if (stage <= 0) return;
        boolean ease = stage >= MAX_STAGE || ThreadLocalRandom.current().nextDouble() < DEATH_EASE_CHANCE;
        int next = ease ? Math.max(1, stage - 1) : stage;
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(STAGE, PersistentDataType.INTEGER, next);
        pdc.set(LEFT, PersistentDataType.LONG, stageMs);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!isWounded(event.getPlayer())) return;
        event.getPlayer().sendActionBar(Component.text("The cold blade's wound is still with you.",
                NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, true));
    }
}
