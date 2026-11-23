package dev.grapeofwrath.ardanborn.items.rings;

import dev.grapeofwrath.ardanborn.Keys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

/// Ringwraiths sent to hunt a ring-bearer: on a new moon (Narya, Nenya, Vilya or the One Ring), and in
/// the Nether (the One Ring).
///
/// Unlike the servants a betraying ring sends, which appear right beside you, hunting wraiths spawn far
/// off and have to find you. They ride toward you, and attack once they're close. They give up when the
/// hunt runs out, or when you've stopped carrying what they seek for a while.
public final class RingwraithHunts {
    private static final NamespacedKey NEW_MOON_HUNTED = Keys.key("new_moon_hunted");
    private static final long NEW_MOON_PHASE = 4; // 0 is the full moon
    private static final double CARRIED_CHANCE = 0.03;
    private static final double WORN_CHANCE = 0.10;
    private static final long HUNT_TICKS = 6000; // 5 minutes
    private static final int STEP_TICKS = 40;
    private static final int LOST_TRAIL_TICKS = 600; // 30 seconds
    private static final double LOCK_ON = 16;

    private static Plugin plugin;

    private RingwraithHunts() {
    }

    static void start(Plugin plugin) {
        RingwraithHunts.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, RingwraithHunts::newMoonCheck, 1200L, 1200L);
    }

    // ------------------------------------------------------------------ the new moon

    /// Whether it's a new-moon night in this world. Only worlds with a moon (the Overworld) have one.
    static boolean newMoonNight(World world) {
        if (world.getEnvironment() != World.Environment.NORMAL) return false;
        long time = world.getTime();
        return (world.getFullTime() / 24000L) % 8L == NEW_MOON_PHASE && time >= 13000 && time < 23000;
    }

    private static void newMoonCheck() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (Player player : Bukkit.getOnlinePlayers()) {
            World world = player.getWorld();
            if (!newMoonNight(world) || !carriesUnique(player)) continue;
            long night = world.getFullTime() / 24000L;
            var pdc = player.getPersistentDataContainer();
            if (pdc.getOrDefault(NEW_MOON_HUNTED, PersistentDataType.LONG, -1L) == night) continue;
            RingType worn = RingBearer.worn(player);
            double chance = worn != null && worn.unique() ? WORN_CHANCE : CARRIED_CHANCE;
            if (random.nextDouble() >= chance) continue;
            pdc.set(NEW_MOON_HUNTED, PersistentDataType.LONG, night);
            int count = carries(player, RingType.ONE) ? 5 : 3;
            player.sendActionBar(Component.text("The Black Riders are abroad…", NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, true));
            hunt(player, count, 32, 48, RingwraithHunts::carriesUnique,
                    p -> newMoonNight(p.getWorld()));
        }
    }

    // ------------------------------------------------------------------ what a player carries

    /** Whether the player carries a current copy of Narya, Nenya, Vilya or the One Ring. */
    static boolean carriesUnique(Player player) {
        for (ItemStack stack : player.getInventory().getContents()) {
            RingType type = Rings.typeOf(stack);
            if (type != null && type.unique() && !Rings.isStale(stack)) return true;
        }
        return false;
    }

    /** Whether the player carries a current copy of the given ring. */
    static boolean carries(Player player, RingType ring) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (Rings.typeOf(stack) == ring && !Rings.isStale(stack)) return true;
        }
        return false;
    }

    // ------------------------------------------------------------------ the hunt

    /// Sends `count` Ringwraiths after the player, spawning `minDistance`–`maxDistance` blocks away. They
    /// keep after the player while `trail` holds (losing it after 30 seconds without it) and `until`
    /// holds, for at most 5 minutes.
    static void hunt(Player target, int count, int minDistance, int maxDistance, Predicate<Player> trail,
                     Predicate<Player> until) {
        World world = target.getWorld();
        world.playSound(target.getLocation(), Sound.ENTITY_GHAST_SCREAM, 0.6f, 0.5f);
        List<Entity> pack = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Location at = farSpot(target, minDistance, maxDistance);
            if (at == null) at = RingBearer.spawnNear(target);
            pack.addAll(spawnRingwraith(at));
        }
        UUID id = target.getUniqueId();
        final BukkitTask[] task = new BukkitTask[1];
        final int[] elapsed = {0, 0}; // ticks hunted, ticks without the trail
        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            elapsed[0] += STEP_TICKS;
            Player player = Bukkit.getPlayer(id);
            boolean onTrail = player != null && player.getWorld().equals(world) && !player.isDead()
                    && trail.test(player);
            elapsed[1] = onTrail ? 0 : elapsed[1] + STEP_TICKS;
            boolean over = elapsed[0] >= HUNT_TICKS || elapsed[1] >= LOST_TRAIL_TICKS
                    || player == null || !until.test(player) || pack.stream().noneMatch(Entity::isValid);
            if (over) {
                pack.forEach(RingwraithHunts::fade);
                task[0].cancel();
                return;
            }
            if (!onTrail) return;
            for (Entity entity : pack) {
                if (!(entity instanceof WitherSkeleton wraith) || !wraith.isValid()) continue;
                if (wraith.getLocation().distanceSquared(player.getLocation()) <= LOCK_ON * LOCK_ON) {
                    wraith.setTarget(player);
                } else {
                    // A mounted wraith steers its horse, so the horse is what has to move.
                    Entity vehicle = wraith.getVehicle();
                    Mob mover = vehicle instanceof Mob horse && horse.isValid() ? horse : wraith;
                    mover.getPathfinder().moveTo(player.getLocation(), 1.6);
                }
            }
        }, STEP_TICKS, STEP_TICKS);
    }

    private static void fade(Entity entity) {
        if (!entity.isValid()) return;
        entity.getWorld().spawnParticle(Particle.LARGE_SMOKE, entity.getLocation().add(0, 1, 0), 20, 0.4, 0.6, 0.4,
                0.02);
        entity.remove();
    }

    /// A spot `min`–`max` blocks from the player, near their height, with room for a mounted rider.
    private static Location farSpot(Player player, int min, int max) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Location origin = player.getLocation();
        for (int attempt = 0; attempt < 16; attempt++) {
            double angle = random.nextDouble(Math.PI * 2);
            double distance = random.nextDouble(min, max);
            Location column = origin.clone().add(Math.cos(angle) * distance, 0, Math.sin(angle) * distance);
            if (!column.getWorld().isChunkLoaded(column.getBlockX() >> 4, column.getBlockZ() >> 4)) continue;
            for (int dy = 0; dy <= 12; dy++) {
                for (int sign : new int[]{1, -1}) {
                    Block feet = column.clone().add(0, dy * sign, 0).getBlock();
                    if (feet.isPassable() && !feet.isLiquid() && feet.getRelative(0, 1, 0).isPassable()
                            && feet.getRelative(0, 2, 0).isPassable()
                            && feet.getRelative(0, -1, 0).getType().isSolid()) {
                        return feet.getLocation().add(0.5, 0, 0.5);
                    }
                    if (dy == 0) break;
                }
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ the riders

    /// A Ringwraith on its skeleton horse at `at`: the rider, then the horse. Both are servants of the
    /// Enemy: they drop nothing and don't persist.
    static List<Entity> spawnRingwraith(Location at) {
        World world = at.getWorld();
        SkeletonHorse steed = world.spawn(at, SkeletonHorse.class, horse -> {
            horse.setTamed(true);
            horse.getPersistentDataContainer().set(RingBearer.SERVANT, PersistentDataType.BOOLEAN, true);
            horse.setPersistent(false);
        });
        WitherSkeleton wraith = world.spawn(at, WitherSkeleton.class, mob -> {
            mob.customName(Component.text("Ringwraith", NamedTextColor.DARK_GRAY));
            markServant(mob);
            EntityEquipment gear = mob.getEquipment();
            gear.setHelmet(blackLeather(Material.LEATHER_HELMET));
            gear.setChestplate(blackLeather(Material.LEATHER_CHESTPLATE));
            gear.setLeggings(blackLeather(Material.LEATHER_LEGGINGS));
            gear.setBoots(blackLeather(Material.LEATHER_BOOTS));
            for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
                    EquipmentSlot.FEET, EquipmentSlot.HAND)) {
                gear.setDropChance(slot, 0f);
            }
        });
        steed.addPassenger(wraith);
        return List.of(wraith, steed);
    }

    static void markServant(WitherSkeleton mob) {
        mob.setCustomNameVisible(true);
        mob.setPersistent(false);
        mob.setRemoveWhenFarAway(false);
        mob.getPersistentDataContainer().set(RingBearer.SERVANT, PersistentDataType.BOOLEAN, true);
    }

    private static ItemStack blackLeather(Material material) {
        ItemStack item = ItemStack.of(material);
        if (item.getItemMeta() instanceof LeatherArmorMeta meta) {
            meta.setColor(Color.fromRGB(0x111111));
            item.setItemMeta(meta);
        }
        return item;
    }
}
