package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Chestplate: the night sky guards you. For Peredhil, anywhere at night.
public final class StarWovenEffect extends PieceEffect implements Listener {
    public StarWovenEffect(String setId) {
        super("star_woven", "Star-woven", setId, EquipmentSlot.CHEST, OriginCheck.PEREDHIL);
    }

    @Override
    protected List<String> lines() {
        return List.of("Tougher beneath the night sky", "Peredhil: anywhere at night");
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || !OriginAbilities.isNight(player.getWorld())) return;
        if (Effects.UNSTOPPABLE.contains(event.getCause()) || !worn(player)) return;
        if (player.getLocation().getBlock().getLightFromSky() < 15 && !own(player)) return;
        event.setDamage(event.getDamage() * ItemPower.reduction(id, 0.12));
    }
}
