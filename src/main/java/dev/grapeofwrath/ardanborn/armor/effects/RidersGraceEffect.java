package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Leggings: rider and mount take less harm. A Peredhil's mount slowly heals.
public final class RidersGraceEffect extends PieceEffect implements Listener {
    public RidersGraceEffect(String setId) {
        super("riders_grace", "Rider's Grace", setId, EquipmentSlot.LEGS, OriginCheck.PEREDHIL);
    }

    @Override
    protected List<String> lines() {
        return List.of("You and your mount are tougher", "while riding", "Peredhil: your mount slowly heals");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        if (every(100) && player.getVehicle() instanceof LivingEntity mount && !(mount instanceof Player) && own(player)) {
            Effects.heal(mount, ItemPower.amount(id, 2));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (Effects.UNSTOPPABLE.contains(event.getCause())) return;
        boolean guarded = event.getEntity() instanceof Player player
                ? player.getVehicle() instanceof LivingEntity && worn(player)
                : event.getEntity() instanceof LivingEntity mount && mount.getPassengers().stream()
                        .anyMatch(rider -> rider instanceof Player p && worn(p));
        if (guarded) event.setDamage(event.getDamage() * ItemPower.reduction(id, 0.15));
    }
}
