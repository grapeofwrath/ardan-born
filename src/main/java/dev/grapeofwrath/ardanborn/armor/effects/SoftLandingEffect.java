package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// Boots: you and your mount land lightly.
public final class SoftLandingEffect extends PieceEffect implements Listener {
    public SoftLandingEffect(String setId) {
        super("soft_landing", "Soft Landing", setId, EquipmentSlot.FEET, OriginCheck.PEREDHIL);
    }

    @Override
    protected List<String> lines() {
        return List.of("Softer falls, for your mount too", "Peredhil: even softer");
    }

    private @Nullable Player wearer(Entity entity) {
        if (entity instanceof Player player) return worn(player) ? player : null;
        for (Entity rider : entity.getPassengers()) {
            if (rider instanceof Player player && worn(player)) return player;
        }
        return null;
    }

    @EventHandler(ignoreCancelled = true)
    public void onFall(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        Player player = wearer(event.getEntity());
        if (player != null) event.setDamage(event.getDamage() * (own(player) ? 0.25 : 0.5));
    }
}
