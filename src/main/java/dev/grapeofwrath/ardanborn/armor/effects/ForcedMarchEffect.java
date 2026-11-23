package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Leggings: run on and on.
public final class ForcedMarchEffect extends PieceEffect implements Listener {
    public ForcedMarchEffect(String setId) {
        super("forced_march", "Forced March", setId, EquipmentSlot.LEGS, OriginCheck.URUK_HAI);
    }

    @Override
    protected List<String> lines() {
        return List.of("Sprinting uses less hunger", "Uruk-hai: even less");
    }

    @EventHandler(ignoreCancelled = true)
    public void onExhaust(EntityExhaustionEvent event) {
        if (event.getExhaustionReason() != EntityExhaustionEvent.ExhaustionReason.SPRINT) return;
        if (!(event.getEntity() instanceof Player player) || !worn(player)) return;
        event.setExhaustion(event.getExhaustion() * (own(player) ? 0.5f : 0.75f));
    }
}
