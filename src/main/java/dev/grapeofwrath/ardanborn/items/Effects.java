package dev.grapeofwrath.ardanborn.items;

import dev.grapeofwrath.ardanborn.ArdanBorn;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/// Small pieces shared by race items and armor effects.
public final class Effects {
    /// Harm no item should soften.
    public static final Set<EntityDamageEvent.DamageCause> UNSTOPPABLE = EnumSet.of(
            EntityDamageEvent.DamageCause.VOID, EntityDamageEvent.DamageCause.KILL,
            EntityDamageEvent.DamageCause.SUICIDE, EntityDamageEvent.DamageCause.STARVATION);

    /// True while an item is dealing secondary damage, which must not set off more of it.
    private static boolean striking = false;

    private Effects() {}

    public static Plugin plugin() {
        return JavaPlugin.getPlugin(ArdanBorn.class);
    }

    public static void potion(LivingEntity target, PotionEffectType type, int ticks, int amplifier) {
        target.addPotionEffect(new PotionEffect(type, ticks, amplifier, true, false, true));
    }

    public static void heal(LivingEntity target, double amount) {
        if (!target.isDead()) target.heal(amount, EntityRegainHealthEvent.RegainReason.CUSTOM);
    }

    public static double maxHealth(LivingEntity entity) {
        AttributeInstance max = entity.getAttribute(Attribute.MAX_HEALTH);
        return max == null ? entity.getHealth() : max.getValue();
    }

    public static boolean striking() {
        return striking;
    }

    /// Secondary damage credited to the player. It isn't a melee hit, so melee bonuses don't add to it.
    public static void strike(LivingEntity victim, double amount, Player by, DamageType type) {
        if (victim.isDead()) return;
        boolean before = striking;
        striking = true;
        try {
            victim.damage(amount, DamageSource.builder(type).withCausingEntity(by).withDirectEntity(by).build());
        } finally {
            striking = before;
        }
    }

    public static void strike(LivingEntity victim, double amount, Player by) {
        strike(victim, amount, by, DamageType.INDIRECT_MAGIC);
    }

    /// The player behind a melee hit, or null. Secondary damage never counts.
    public static @Nullable Player meleeAttacker(EntityDamageByEntityEvent event) {
        return !striking && event.getDamager() instanceof Player player && OriginAbilities.isMelee(event) ? player : null;
    }

    /// As [#meleeAttacker], counting sweeping hits too.
    public static @Nullable Player swingAttacker(EntityDamageByEntityEvent event) {
        if (striking || !(event.getDamager() instanceof Player player)) return null;
        return OriginAbilities.isMelee(event)
                || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK ? player : null;
    }

    /// The player whose arrow (not trident) this is, or null.
    public static @Nullable Player archer(EntityDamageByEntityEvent event) {
        return event.getDamager() instanceof AbstractArrow arrow && !(arrow instanceof Trident)
                && arrow.getShooter() instanceof Player player ? player : null;
    }

    /// The mob behind a melee hit, or null.
    public static @Nullable Mob meleeMob(EntityDamageByEntityEvent event) {
        return event.getDamager() instanceof Mob mob && OriginAbilities.isMelee(event) ? mob : null;
    }

    public static List<LivingEntity> enemiesNear(Entity center, double range) {
        return enemiesNear(center.getLocation(), range);
    }

    public static List<LivingEntity> enemiesNear(Location center, double range) {
        List<LivingEntity> found = new ArrayList<>();
        for (LivingEntity near : center.getNearbyLivingEntities(range)) {
            if (near instanceof Enemy && !near.isDead()
                    && near.getLocation().distanceSquared(center) <= range * range) found.add(near);
        }
        return found;
    }

    /// The player and everyone within `range` of them.
    public static List<Player> company(Player player, double range) {
        List<Player> found = new ArrayList<>();
        found.add(player);
        for (Player near : player.getLocation().getNearbyPlayers(range)) {
            if (near != player && near.getGameMode() != GameMode.SPECTATOR) found.add(near);
        }
        return found;
    }

    /// Throws a mob away from a place. Runs a tick later, after the hit's own knockback.
    public static void push(LivingEntity mob, Location from, double strength) {
        Vector away = mob.getLocation().toVector().subtract(from.toVector()).setY(0);
        if (away.lengthSquared() < 1.0e-4 || mob instanceof Boss) return;
        Vector velocity = away.normalize().multiply(strength).setY(0.4);
        Bukkit.getScheduler().runTask(plugin(), () -> {
            if (mob.isValid()) mob.setVelocity(velocity);
        });
    }

    public static boolean onBlock(Player player, Predicate<Material> test) {
        return test.test(player.getLocation().subtract(0, 0.2, 0).getBlock().getType());
    }

    /// Adds, changes or (for 0) removes a modifier. Transient, so it is never saved with the player and
    /// can't linger if the server stops.
    public static void modifier(Player player, Attribute attribute, NamespacedKey key, double amount,
                                AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        AttributeModifier current = instance.getModifier(key);
        if (current == null ? amount == 0 : current.getAmount() == amount) return;
        if (current != null) instance.removeModifier(key);
        if (amount != 0) instance.addTransientModifier(new AttributeModifier(key, amount, operation));
    }

    public static void scale(Player player, Attribute attribute, NamespacedKey key, double amount) {
        modifier(player, attribute, key, amount, AttributeModifier.Operation.ADD_SCALAR);
    }
}
