package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Boots: boats you ride are unharmed, and fish bite sooner.
public final class NumenoreanEffect extends PieceEffect implements Listener {
    public NumenoreanEffect(String setId) {
        super("numenorean", "Númenórean", setId, EquipmentSlot.FEET, OriginCheck.DUNEDAIN);
    }

    @Override
    protected List<String> lines() {
        return List.of("Your boats are safe, and fish bite sooner", "Dúnedain: sooner still");
    }

    private boolean guarded(Entity vehicle) {
        if (!(vehicle instanceof Boat)) return false;
        for (Entity passenger : vehicle.getPassengers()) {
            if (passenger instanceof Player player && worn(player)) return true;
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(VehicleDamageEvent event) {
        if (guarded(event.getVehicle())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDestroy(VehicleDestroyEvent event) {
        if (guarded(event.getVehicle())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCast(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (event.getState() != PlayerFishEvent.State.FISHING || !worn(player)) return;
        double factor = own(player) ? 0.8 : 0.9;
        FishHook hook = event.getHook();
        hook.setWaitTime((int) (hook.getMinWaitTime() * factor), (int) (hook.getMaxWaitTime() * factor));
    }
}
