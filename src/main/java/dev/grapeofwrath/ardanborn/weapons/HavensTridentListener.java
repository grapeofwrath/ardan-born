package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

/// Smithing and effects of the Spear of the Havens (see [HavensTrident]).
public final class HavensTridentListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepare(PrepareSmithingEvent event) {
        SmithingInventory inventory = event.getInventory();
        if (!HavensTrident.isTemplate(inventory.getInputTemplate())) return;
        ItemStack base = inventory.getInputEquipment();
        ItemStack addition = inventory.getInputMineral();
        boolean valid = base != null && base.getType() == Material.TRIDENT
                && !HavensTrident.isSpear(base)
                && (addition == null || addition.isEmpty());
        event.setResult(valid ? HavensTrident.upgrade(base.asOne()) : null);
    }

    /// Always returns: a thrown spear without Loyalty flies back as if it had Loyalty I.
    @EventHandler(ignoreCancelled = true)
    public void onThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) return;
        if (!HavensTrident.isSpear(trident.getItemStack())) return;
        if (trident.getLoyaltyLevel() == 0) trident.setLoyaltyLevel(1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player wielder;
        ItemStack spear;
        if (event.getDamager() instanceof Trident trident && trident.getShooter() instanceof Player thrower) {
            wielder = thrower;
            spear = trident.getItemStack();
            if (HavensTrident.isSpear(spear)) callLightning(trident, thrower, event);
        } else if (event.getDamager() instanceof Player player
                && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            wielder = player;
            spear = player.getInventory().getItemInMainHand();
        } else {
            return;
        }
        if (HavensTrident.isSpear(spear) && OriginAbilities.TIDES_FURY.hasAbility(wielder)) {
            event.setDamage(event.getDamage() * HavensTrident.TELERI_BONUS);
        }
    }

    /// Channeling outside a thunderstorm, for foes in water or rain. In a storm vanilla Channeling applies.
    private static void callLightning(Trident trident, Player thrower, EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (!trident.getItemStack().containsEnchantment(Enchantment.CHANNELING)) return;
        if (victim.getWorld().isThundering() || !victim.isInWaterOrRain()) return;
        LightningStrike strike = victim.getWorld().strikeLightning(victim.getLocation());
        strike.setCausingPlayer(thrower);
    }
}
