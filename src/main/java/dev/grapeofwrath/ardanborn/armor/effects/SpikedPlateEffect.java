package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Chestplate: whoever strikes you is hurt in turn.
public final class SpikedPlateEffect extends PieceEffect implements Listener {
    public SpikedPlateEffect(String setId) {
        super("spiked_plate", "Spiked Plate", setId, EquipmentSlot.CHEST, OriginCheck.URUK_HAI);
    }

    @Override
    protected List<String> lines() {
        return List.of("Mobs that strike you are hurt", "Uruk-hai: and hurt more");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Mob mob = Effects.meleeMob(event);
        if (mob == null || !(event.getEntity() instanceof Player player) || !worn(player)) return;
        Effects.strike(mob, ItemPower.amount(id, event.getFinalDamage() * (own(player) ? 0.25 : 0.15)), player, DamageType.THORNS);
    }
}
