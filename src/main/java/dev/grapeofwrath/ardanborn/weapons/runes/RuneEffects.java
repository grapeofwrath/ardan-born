package dev.grapeofwrath.ardanborn.weapons.runes;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

/// What each [Rune] does. Runes work for whoever holds the item; only inscribing them is limited to Smiths.
public final class RuneEffects implements Listener {
    private static final double WESTERNESSE_BONUS = 1.3;
    private static final int FROST_TICKS = 40;
    private static final double WARNING_RANGE = 16;
    /// On arrows shot from a runed bow or crossbow: the bow's runes, so they apply when the arrow lands.
    private static final NamespacedKey PROJECTILE_RUNES = Keys.key("projectile_runes");
    private static final Particle.DustOptions WARNING_GLOW = new Particle.DustOptions(Color.fromRGB(0x6FB8FF), 0.8f);

    /// Delving: what each ore's raw drop smelts into.
    private static final Map<Material, Material> SMELTED = Map.of(
            Material.RAW_IRON, Material.IRON_INGOT,
            Material.RAW_COPPER, Material.COPPER_INGOT,
            Material.RAW_GOLD, Material.GOLD_INGOT);
    private static final List<Material> DELVING_ORES = List.of(
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE);

    void start(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, this::warn, 20L, 20L);
    }

    // ------------------------------------------------------------------ Westernesse and Frost (melee)

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity victim)) return;
        List<Rune> runes;
        boolean melee = e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;
        if (e.getDamager() instanceof Player player && melee) {
            runes = Runes.on(player.getInventory().getItemInMainHand());
        } else if (e.getDamager() instanceof Trident trident) {
            runes = Runes.on(trident.getItemStack());
        } else if (e.getDamager() instanceof Projectile projectile) {
            runes = projectileRunes(projectile);
        } else {
            return;
        }
        if (runes.isEmpty()) return;

        if (runes.contains(Rune.WESTERNESSE) && melee && OriginAbilities.UNDEAD.contains(victim.getType())) {
            e.setDamage(e.getDamage() * WESTERNESSE_BONUS);
        }
        if (runes.contains(Rune.FROST)) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, FROST_TICKS, 0, true, true, true));
            victim.getWorld().spawnParticle(Particle.SNOWFLAKE, victim.getLocation().add(0, 1, 0), 8, 0.3, 0.4, 0.3, 0.01);
        }
    }

    // ------------------------------------------------------------------ Frost (bows and crossbows)

    @EventHandler(ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent e) {
        List<Rune> runes = Runes.on(e.getBow());
        if (runes.isEmpty() || !(e.getProjectile() instanceof Projectile projectile)) return;
        List<String> ids = runes.stream().map(Rune::id).toList();
        projectile.getPersistentDataContainer().set(PROJECTILE_RUNES, PersistentDataType.LIST.strings(), ids);
    }

    private static List<Rune> projectileRunes(Projectile projectile) {
        List<String> ids = projectile.getPersistentDataContainer().get(PROJECTILE_RUNES, PersistentDataType.LIST.strings());
        if (ids == null) return List.of();
        return ids.stream().map(Rune::byId).filter(r -> r != null).toList();
    }

    // ------------------------------------------------------------------ Warning (Sting)

    private void warn() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Runes.has(player.getInventory().getItemInMainHand(), Rune.WARNING)) continue;
            boolean near = false;
            for (Entity entity : player.getNearbyEntities(WARNING_RANGE, WARNING_RANGE / 2, WARNING_RANGE)) {
                if (entity instanceof Monster) {
                    near = true;
                    break;
                }
            }
            if (!near) continue;
            player.sendActionBar(Component.text("Your blade glows blue", Rune.WARNING.color()));
            Location hand = player.getLocation().add(player.getLocation().getDirection().multiply(0.5)).add(0, 1.0, 0);
            player.getWorld().spawnParticle(Particle.DUST, hand, 6, 0.15, 0.25, 0.15, 0, WARNING_GLOW);
        }
    }

    // ------------------------------------------------------------------ Delving

    /// Runs for every block a player breaks, including blocks broken through `Player#breakBlock`, so Fortune's
    /// extra drops are smelted too.
    @EventHandler(ignoreCancelled = true)
    public void onDrops(BlockDropItemEvent e) {
        if (!DELVING_ORES.contains(e.getBlockState().getType())) return;
        ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
        if (!Runes.has(tool, Rune.DELVING)) return;
        for (Item drop : e.getItems()) {
            Material smelted = SMELTED.get(drop.getItemStack().getType());
            if (smelted != null) drop.setItemStack(drop.getItemStack().withType(smelted));
        }
    }
}
