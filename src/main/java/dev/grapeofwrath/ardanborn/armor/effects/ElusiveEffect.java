package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.CustomArmor;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/// Chestplate (the Cloak of Lórien): a mob's blow or arrow sometimes passes through only leaves. Sindar
/// leave the attacker tangled.
public final class ElusiveEffect implements SetEffect, Listener {
    private static final double CHANCE = 0.10;
    private static final double WOODLAND_CHANCE = 0.15;
    private static final int TANGLE_TICKS = 60;

    private final String setId;

    public ElusiveEffect(String setId) {
        this.setId = setId;
    }

    @Override
    public String name() {
        return "Elusive";
    }

    @Override
    public List<Component> describe(ArmorTier tier) {
        return List.of(
                Component.text("10% chance a mob's blow or arrow"),
                Component.text("misses you (15% in woodland)"),
                Component.text("Sindar: the attacker is tangled"));
    }

    /// Runs early, so cancelling stops the damage before other plugins and powers react to it.
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Mob attacker = attackingMob(event.getDamager());
        if (attacker == null) return;
        if (CustomArmor.wornTier(player, EquipmentSlot.CHEST, setId) == null) return;
        double chance = OriginAbilities.inWoodland(player) ? WOODLAND_CHANCE : CHANCE;
        if (ThreadLocalRandom.current().nextDouble() >= chance) return;

        event.setCancelled(true);
        if (event.getDamager() instanceof Projectile projectile) projectile.remove();
        player.getWorld().spawnParticle(Particle.CHERRY_LEAVES, player.getLocation().add(0, 1, 0), 20, 0.4, 0.6, 0.4, 0);
        if (OriginCheck.is(player, OriginCheck.SINDAR)) {
            attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, TANGLE_TICKS, 1, true, true, true));
        }
    }

    /// The mob behind a hit: the attacker itself, or whoever shot the projectile. Players never count.
    private static @Nullable Mob attackingMob(Entity damager) {
        if (damager instanceof Mob mob) return mob;
        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Mob shooter) return shooter;
        return null;
    }
}
