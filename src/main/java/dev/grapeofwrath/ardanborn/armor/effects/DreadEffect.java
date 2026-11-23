package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Helmet: your blows sometimes sap the foe's strength.
public final class DreadEffect extends PieceEffect implements Listener {
    public DreadEffect(String setId) {
        super("dread", "Dread", setId, EquipmentSlot.HEAD, OriginCheck.URUK_HAI);
    }

    @Override
    protected List<String> lines() {
        return List.of("At times your melee hits weaken the foe", "Uruk-hai: more often");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.meleeAttacker(event);
        if (player == null || !(event.getEntity() instanceof Mob mob) || !worn(player)) return;
        if (ItemPower.roll(id, own(player) ? 0.20 : 0.10)) Effects.potion(mob, PotionEffectType.WEAKNESS, 60, 0);
    }
}
