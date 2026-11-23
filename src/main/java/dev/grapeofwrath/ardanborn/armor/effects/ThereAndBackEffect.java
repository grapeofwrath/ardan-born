package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Boots: the long road wearies you less.
public final class ThereAndBackEffect extends PieceEffect implements Listener {
    public ThereAndBackEffect(String setId) {
        super("there_and_back", "There and Back", setId, EquipmentSlot.FEET, OriginCheck.HOBBIT);
    }

    @Override
    protected List<String> lines() {
        return List.of("You hunger less for sprinting", "Hobbits: and less again");
    }

    @EventHandler(ignoreCancelled = true)
    public void onExhaust(EntityExhaustionEvent event) {
        if (event.getExhaustionReason() != EntityExhaustionEvent.ExhaustionReason.SPRINT) return;
        if (!(event.getEntity() instanceof Player player) || !worn(player)) return;
        event.setExhaustion(event.getExhaustion() * (own(player) ? 0.6f : 0.8f));
    }
}
