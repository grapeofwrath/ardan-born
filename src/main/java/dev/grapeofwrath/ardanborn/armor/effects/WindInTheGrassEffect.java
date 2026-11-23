package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.mounts.Riding;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Chestplate: more experience from kills made in the saddle.
public final class WindInTheGrassEffect extends PieceEffect implements Listener {
    public WindInTheGrassEffect(String setId) {
        super("wind_in_the_grass", "Wind in the Grass", setId, EquipmentSlot.CHEST, OriginCheck.ROHIRRIM);
    }

    @Override
    protected List<String> lines() {
        return List.of("Slaying from the saddle teaches you more", "Rohirrim: more still");
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null || event.getDroppedExp() <= 0 || !Riding.mounted(player) || !worn(player)) return;
        event.setDroppedExp((int) Math.ceil(event.getDroppedExp() * (own(player) ? 1.2 : 1.1)));
    }
}
