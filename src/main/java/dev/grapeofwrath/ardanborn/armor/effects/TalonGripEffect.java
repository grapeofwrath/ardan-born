package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Leggings: melee hits sometimes lift the foe off its feet.
public final class TalonGripEffect extends PieceEffect implements Listener {
    /// Lifts a mob about two blocks.
    private static final double LIFT = 0.55;

    public TalonGripEffect(String setId) {
        super("talon_grip", "Talon-grip", setId, EquipmentSlot.LEGS, OriginCheck.GREAT_EAGLE);
    }

    @Override
    protected List<String> lines() {
        return List.of("Melee hits sometimes lift the foe", "Eagles: more often");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.meleeAttacker(event);
        if (player == null || !(event.getEntity() instanceof Mob mob) || mob instanceof Boss || !worn(player)) return;
        if (!ItemPower.roll(id, own(player) ? 0.25 : 0.15)) return;
        // After the hit's own knockback.
        Bukkit.getScheduler().runTask(Effects.plugin(), () -> {
            if (mob.isValid()) mob.setVelocity(mob.getVelocity().setY(LIFT));
        });
    }
}
