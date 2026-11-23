package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Helmet: long shots strike harder.
public final class FarSightedEffect extends PieceEffect implements Listener {
    public FarSightedEffect(String setId) {
        super("far_sighted", "Far-sighted", setId, EquipmentSlot.HEAD, OriginCheck.SINDAR);
    }

    @Override
    protected List<String> lines() {
        return List.of("Long shots deal more damage", "Sindar: even more");
    }

    @EventHandler(ignoreCancelled = true)
    public void onArrow(EntityDamageByEntityEvent event) {
        Player player = Effects.archer(event);
        if (player == null || !player.getWorld().equals(event.getEntity().getWorld())) return;
        if (player.getLocation().distanceSquared(event.getEntity().getLocation()) < 400 || !worn(player)) return;
        event.setDamage(event.getDamage() * ItemPower.bonus(id, own(player) ? 0.20 : 0.10));
    }
}
