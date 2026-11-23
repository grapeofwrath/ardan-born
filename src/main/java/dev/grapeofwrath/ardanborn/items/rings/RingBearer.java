package dev.grapeofwrath.ardanborn.items.rings;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.Ores;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import dev.grapeofwrath.ardanborn.weapons.Miruvor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/// Wearing a Ring of Power: putting it on and off, its powers, and the Shadow it builds.
///
/// A ring is worn in the off-hand; right-click puts it on or takes it off. While it's on, Shadow (0–100,
/// kept on the player) rises at the ring's rate; while no ring is on, it falls 1 every 10 seconds.
/// - 25+: whispers.
/// - 50+: Darkness pulses and Hunger.
/// - 75+: Wither while worn, and when hurt the ring may put itself on.
/// - 100: the ring betrays you. It drops away, you wither, Shadow falls to 50, and the Enemy's servants
///   come for you (Ringwraiths, for the One Ring).
public final class RingBearer implements Listener {
    private static final NamespacedKey SHADOW = Keys.key("shadow");
    private static final NamespacedKey WORN = Keys.key("ring_worn");
    /// On mobs sent by a betraying ring, so they drop nothing and leave when their hunt ends.
    public static final NamespacedKey SERVANT = Keys.key("ring_servant");

    private static final double MAX = 100;
    private static final double DECAY_PER_SECOND = 0.1;
    private static final double DEATHLESS_COST = 40;
    private static final double GREED_PER_ORE = 1;
    private static final long HUNT_TICKS = 2400; // 2 minutes
    private static final double EYE_SHADOW_PER_SECOND = 0.5;
    private static final double MORDOR_RANGE = 24;
    private static final int NAZGUL_EVERY_SECONDS = 180;
    private static final double NAZGUL_CHANCE = 0.3;
    private static final Set<EntityDamageEvent.DamageCause> UNSTOPPABLE = EnumSet.of(
            EntityDamageEvent.DamageCause.VOID, EntityDamageEvent.DamageCause.KILL,
            EntityDamageEvent.DamageCause.SUICIDE);
    private static final List<String> WHISPERS = List.of(
            "…put it on…", "…to no other was it given…", "…the Eye does not sleep…",
            "…wherefore take it off?…", "…they covet it, all of them…");

    private Plugin plugin;
    /// Seconds since each Vilya bearer's last healing pulse.
    private final Map<UUID, Integer> vilyaPulse = new HashMap<>();
    /// Seconds each player has spent under the Eye (carrying the One Ring in the Nether), this visit.
    private final Map<UUID, Integer> eyeSeconds = new HashMap<>();

    void start(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, this::tickAll, 20L, 20L);
        Miruvor.onDrink(player -> addShadow(player, -15));
        RingwraithHunts.start(plugin);
    }

    // ------------------------------------------------------------------ Shadow

    public static double shadow(Player player) {
        return player.getPersistentDataContainer().getOrDefault(SHADOW, PersistentDataType.DOUBLE, 0.0);
    }

    public static void setShadow(Player player, double value) {
        double clamped = Math.max(0, Math.min(MAX, value));
        player.getPersistentDataContainer().set(SHADOW, PersistentDataType.DOUBLE, clamped);
    }

    public static void addShadow(Player player, double amount) {
        setShadow(player, shadow(player) + amount);
    }

    /// Shadow gains are halved for Hobbits, whom the Ring could not easily master.
    private static double gain(Player player, double amount) {
        return OriginCheck.is(player, OriginCheck.HOBBIT) ? amount / 2 : amount;
    }

    // ------------------------------------------------------------------ wearing

    /** The ring a player is wearing right now, or null: on, and held in the off-hand. */
    public static @Nullable RingType worn(Player player) {
        if (!player.getPersistentDataContainer().getOrDefault(WORN, PersistentDataType.BOOLEAN, false)) return null;
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (Rings.isStale(offHand)) return null;
        return Rings.typeOf(offHand);
    }

    private static void setWorn(Player player, boolean worn) {
        player.getPersistentDataContainer().set(WORN, PersistentDataType.BOOLEAN, worn);
    }

    @EventHandler
    public void onToggle(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        RingType type = Rings.typeOf(event.getItem());
        if (type == null) return;
        Player player = event.getPlayer();
        event.setCancelled(true);
        if (event.getHand() != EquipmentSlot.OFF_HAND) {
            player.sendActionBar(Component.text("The ring is worn from the off-hand.", NamedTextColor.GRAY));
            return;
        }
        if (Rings.isStale(event.getItem())) {
            player.getInventory().setItemInOffHand(null);
            Rings.crumbleMessage(player);
            return;
        }
        boolean on = worn(player) == null;
        setWorn(player, on);
        player.playSound(player.getLocation(), on ? Sound.BLOCK_SCULK_SHRIEKER_SHRIEK : Sound.BLOCK_CHAIN_PLACE,
                0.4f, on ? 1.6f : 1.2f);
        player.sendActionBar(Component.text(on ? "The ring is on your hand." : "You put the ring from you.", type.color()));
    }

    // ------------------------------------------------------------------ every second

    private void tickAll() {
        for (Player player : Bukkit.getOnlinePlayers()) tick(player);
    }

    private void tick(Player player) {
        boolean eye = underTheEye(player);
        theEye(player, eye);
        RingType ring = worn(player);
        double before = shadow(player);
        if (ring == null) {
            // Taken off, or moved out of the off-hand: it's off.
            if (player.getPersistentDataContainer().has(WORN)) setWorn(player, false);
            if (eye) {
                // The Eye sees the Ring in the Nether whether it's worn or not.
                addShadow(player, gain(player, EYE_SHADOW_PER_SECOND));
                applyCurse(player, shadow(player));
                player.sendActionBar(Component.text("The Eye is upon you. Shadow: " + (int) shadow(player),
                        NamedTextColor.DARK_RED));
                return;
            }
            if (before > 0) {
                setShadow(player, before - DECAY_PER_SECOND);
                if ((int) before != (int) shadow(player) && (int) before % 10 == 0) showShadow(player, null);
            }
            return;
        }
        addShadow(player, gain(player, ring.shadowPerSecond() + (eye ? EYE_SHADOW_PER_SECOND : 0)));
        double shadow = shadow(player);
        applyPowers(player, ring);
        applyCurse(player, shadow);
        if (shadow >= MAX) {
            betray(player, ring);
            return;
        }
        showShadow(player, ring);
    }

    // ------------------------------------------------------------------ the Eye (the One Ring in the Nether)

    /** Carrying the One Ring (worn or not) in the Nether, where the Eye can see it. */
    public static boolean underTheEye(Player player) {
        return player.getWorld().getEnvironment() == World.Environment.NETHER
                && RingwraithHunts.carries(player, RingType.ONE);
    }

    /// While the Eye is on you: the burden slows you, the Nether's creatures come for you every few
    /// seconds, and now and then Ringwraiths are sent to hunt you down.
    private void theEye(Player player, boolean eye) {
        UUID id = player.getUniqueId();
        if (!eye) {
            eyeSeconds.remove(id);
            return;
        }
        Integer seconds = eyeSeconds.get(id);
        if (seconds == null) {
            player.sendActionBar(Component.text("The Eye is turned upon you.", NamedTextColor.DARK_RED)
                    .decoration(TextDecoration.ITALIC, true));
            player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1f, 0.5f);
            seconds = 0;
        }
        seconds++;
        eyeSeconds.put(id, seconds);

        // The burden: heavier for those not made to bear it.
        effect(player, PotionEffectType.SLOWNESS, OriginCheck.is(player, OriginCheck.HOBBIT) ? 0 : 1);

        if (seconds % 5 == 0) stirMordor(player);
        if (seconds % NAZGUL_EVERY_SECONDS == 0 && ThreadLocalRandom.current().nextDouble() < NAZGUL_CHANCE) {
            player.sendActionBar(Component.text("The Nazgûl are gone forth…", NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, true));
            RingwraithHunts.hunt(player, 2, 24, 32, RingBearer::underTheEye, RingBearer::underTheEye);
        }
    }

    /// The Nether's hostile creatures within reach turn on the bearer, piglins too, gold or no gold.
    /// Zombified piglins are left alone; they'd bring the whole horde.
    private static void stirMordor(Player player) {
        for (Entity near : player.getNearbyEntities(MORDOR_RANGE, MORDOR_RANGE / 2, MORDOR_RANGE)) {
            if (!(near instanceof Mob mob) || !(mob instanceof Enemy) || mob instanceof PigZombie) continue;
            if (isServant(mob) || player.equals(mob.getTarget())) continue;
            if (mob instanceof PiglinAbstract) mob.setMemory(MemoryKey.ANGRY_AT, player.getUniqueId());
            mob.setTarget(player);
        }
    }

    private static void showShadow(Player player, @Nullable RingType ring) {
        int value = (int) shadow(player);
        NamedTextColor color = value >= 75 ? NamedTextColor.DARK_RED : value >= 50 ? NamedTextColor.RED
                : value >= 25 ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY;
        String prefix = ring == null ? "Shadow fading: " : "Shadow: ";
        player.sendActionBar(Component.text(prefix + value, color));
    }

    private static void effect(LivingEntity target, PotionEffectType type, int amplifier) {
        target.addPotionEffect(new PotionEffect(type, 60, amplifier, true, false, true));
    }

    private void applyPowers(Player player, RingType ring) {
        switch (ring) {
            case NARYA -> {
                effect(player, PotionEffectType.FIRE_RESISTANCE, 0);
                for (Entity near : player.getNearbyEntities(8, 4, 8)) {
                    if (near instanceof Player ally) effect(ally, PotionEffectType.STRENGTH, 0);
                }
                effect(player, PotionEffectType.STRENGTH, 0);
            }
            case NENYA -> {
                effect(player, PotionEffectType.WATER_BREATHING, 0);
                effect(player, PotionEffectType.DOLPHINS_GRACE, 0);
                effect(player, PotionEffectType.RESISTANCE, 0);
            }
            case VILYA -> {
                effect(player, PotionEffectType.SPEED, 0);
                effect(player, PotionEffectType.SLOW_FALLING, 0);
                int seconds = vilyaPulse.merge(player.getUniqueId(), 1, Integer::sum);
                if (seconds >= 5) {
                    vilyaPulse.put(player.getUniqueId(), 0);
                    mend(player);
                    for (Entity near : player.getNearbyEntities(8, 4, 8)) {
                        if (near instanceof Player ally) mend(ally);
                    }
                }
            }
            case ONE -> {
                effect(player, PotionEffectType.STRENGTH, 1);
                effect(player, PotionEffectType.RESISTANCE, 0);
            }
            default -> {
                // The Seven and the Nine act through events.
            }
        }
    }

    private static void mend(Player target) {
        var attr = target.getAttribute(Attribute.MAX_HEALTH);
        double max = attr == null ? 20.0 : attr.getValue();
        if (target.isDead() || target.getHealth() >= max) return;
        target.heal(2.0, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.MAGIC);
        target.getWorld().spawnParticle(Particle.HEART, target.getLocation().add(0, 1.8, 0), 2, 0.3, 0.2, 0.3, 0);
    }

    private static void applyCurse(Player player, double shadow) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (shadow >= 25 && random.nextDouble() < 0.08) {
            String whisper = WHISPERS.get(random.nextInt(WHISPERS.size()));
            player.sendMessage(Component.text(whisper, NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true));
            player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 0.5f, 0.6f);
        }
        if (shadow >= 50) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, 0, true, false, true));
            if (random.nextDouble() < 0.07) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 0, true, false, true));
            }
        }
        if (shadow >= 75) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0, true, false, true));
        }
    }

    // ------------------------------------------------------------------ betrayal

    private void betray(Player player, RingType ring) {
        ItemStack stack = player.getInventory().getItemInOffHand().clone();
        player.getInventory().setItemInOffHand(null);
        setWorn(player, false);
        setShadow(player, 50);
        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1, false, true, true));
        player.sendMessage(Component.text("The ring has forsaken you, and is gone from your hand!", NamedTextColor.DARK_RED));
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.6f, 1.4f);

        Location drop = landingSpot(player);
        Item item = player.getWorld().dropItem(drop, stack);
        item.setThrower(player.getUniqueId());

        if (ring == RingType.ONE) {
            for (int i = 0; i < 3; i++) spawnRingwraith(player);
        } else {
            for (int i = 0; i < 2; i++) spawnServant(player);
        }
    }

    /// A random safe spot up to 16 blocks away (air with something solid below), or the player's feet.
    private static Location landingSpot(Player player) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Location origin = player.getLocation();
        for (int attempt = 0; attempt < 12; attempt++) {
            int dx = random.nextInt(-16, 17);
            int dz = random.nextInt(-16, 17);
            for (int dy = 3; dy >= -3; dy--) {
                Block block = origin.clone().add(dx, dy, dz).getBlock();
                if (block.getType().isAir() && block.getRelative(0, -1, 0).getType().isSolid()) {
                    return block.getLocation().add(0.5, 0.2, 0.5);
                }
            }
        }
        return origin;
    }

    private void spawnServant(Player player) {
        Location at = spawnNear(player);
        WitherSkeleton servant = player.getWorld().spawn(at, WitherSkeleton.class, mob -> {
            mob.customName(Component.text("Servant of the Enemy", NamedTextColor.DARK_GRAY));
            RingwraithHunts.markServant(mob);
        });
        servant.setTarget(player);
        dismissLater(servant);
    }

    private void spawnRingwraith(Player player) {
        for (Entity entity : RingwraithHunts.spawnRingwraith(spawnNear(player))) {
            if (entity instanceof WitherSkeleton wraith) wraith.setTarget(player);
            dismissLater(entity);
        }
    }

    static Location spawnNear(Player player) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Location origin = player.getLocation();
        for (int attempt = 0; attempt < 10; attempt++) {
            Location at = origin.clone().add(random.nextInt(-6, 7), 0, random.nextInt(-6, 7));
            Block feet = at.getBlock();
            if (feet.isPassable() && feet.getRelative(0, 1, 0).isPassable()
                    && feet.getRelative(0, 2, 0).isPassable() && feet.getRelative(0, -1, 0).getType().isSolid()) {
                return feet.getLocation().add(0.5, 0, 0.5);
            }
        }
        return origin;
    }

    private void dismissLater(Entity entity) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (entity.isValid()) entity.remove();
        }, HUNT_TICKS);
    }

    @EventHandler
    public void onServantDeath(EntityDeathEvent event) {
        if (!event.getEntity().getPersistentDataContainer().has(SERVANT)) return;
        event.getDrops().clear();
    }

    /** Whether a mob was sent by a betraying ring (a Servant of the Enemy or a Ringwraith). */
    public static boolean isServant(@Nullable Entity entity) {
        return entity != null && entity.getPersistentDataContainer().has(SERVANT);
    }

    // ------------------------------------------------------------------ powers that act through events

    /// The ring wants to be worn: at 75+ Shadow, being hurt may slip it on.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHurt(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (shadow(player) < 75 || worn(player) != null) return;
        if (Rings.typeOf(player.getInventory().getItemInOffHand()) == null) return;
        if (ThreadLocalRandom.current().nextDouble() >= 0.2) return;
        setWorn(player, true);
        player.sendActionBar(Component.text("Unbidden, the ring is on your hand…", NamedTextColor.DARK_RED));
    }

    /// Deathless (the Nine and the One): a killing blow leaves you at 1 heart, for a price.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLethal(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (UNSTOPPABLE.contains(event.getCause())) return;
        RingType ring = worn(player);
        if (ring == null || !ring.deathless()) return;
        if (player.getHealth() - event.getFinalDamage() > 0) return;
        event.setCancelled(true);
        player.setHealth(2.0); // 1 heart
        addShadow(player, DEATHLESS_COST);
        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0, 1, 0), 30, 0.4, 0.6, 0.4, 0.02);
        player.sendActionBar(Component.text("Death came, and the ring denied it.", NamedTextColor.DARK_RED));
    }

    /// The wraith-world (the Nine and the One): monsters far off don't notice you.
    @EventHandler(ignoreCancelled = true)
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Monster monster) || isServant(monster)) return;
        if (!(event.getTarget() instanceof Player player)) return;
        EntityTargetEvent.TargetReason reason = event.getReason();
        if (reason == EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY
                || reason == EntityTargetEvent.TargetReason.CUSTOM) return;
        RingType ring = worn(player);
        if (ring == null || ring.wraithRange() <= 0) return;
        if (!monster.getWorld().equals(player.getWorld())) return;
        if (underTheEye(player)) return; // no hiding from the Eye
        double range = ring.wraithRange();
        if (monster.getLocation().distanceSquared(player.getLocation()) > range * range) event.setCancelled(true);
    }

    /// Narya: your blows set foes alight.
    @EventHandler(ignoreCancelled = true)
    public void onStrike(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (worn(player) != RingType.NARYA) return;
        event.getEntity().setFireTicks(Math.max(event.getEntity().getFireTicks(), 80));
    }

    /// Nenya: what you carry is preserved.
    @EventHandler(ignoreCancelled = true)
    public void onWear(PlayerItemDamageEvent event) {
        if (worn(event.getPlayer()) == RingType.NENYA) event.setCancelled(true);
    }

    /// The Seven: ores yield more, and sometimes gold, but each one feeds your greed.
    @EventHandler(ignoreCancelled = true)
    public void onOre(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        if (worn(player) != RingType.SEVEN) return;
        Material ore = event.getBlockState().getType();
        if (!Ores.isOre(ore)) return;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // Like two more levels of Fortune: ×1 half the time, else ×2 or ×3.
        int roll = random.nextInt(4);
        int multiplier = roll < 2 ? 1 : roll;
        for (Item drop : event.getItems()) {
            ItemStack stack = drop.getItemStack();
            if (stack.getType() == ore) continue; // Silk Touch: the ore block itself
            stack.setAmount(Math.min(stack.getMaxStackSize(), stack.getAmount() * multiplier));
            drop.setItemStack(stack);
        }
        if (random.nextDouble() < 0.15) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                    ItemStack.of(Material.GOLD_NUGGET, random.nextInt(1, 4)));
        }
        if (!OriginCheck.is(player, OriginCheck.NAUGRIM)) addShadow(player, gain(player, GREED_PER_ORE));
    }
}
