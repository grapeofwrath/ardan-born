package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Leggings: an arrow that strikes home quickens your step.
public final class LeafLightEffect extends PieceEffect implements Listener {
    public LeafLightEffect(String setId) {
        super("leaf_light", "Leaf-light", setId, EquipmentSlot.LEGS, OriginCheck.SILVAN);
    }

    @Override
    protected List<String> lines() {
        return List.of("When your arrow hits you have Speed", "Silvan Elves: swifter in woodland");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArrow(EntityDamageByEntityEvent event) {
        Player player = Effects.archer(event);
        if (player == null || event.getEntity() == player || !(event.getEntity() instanceof LivingEntity) || !worn(player)) return;
        Effects.potion(player, PotionEffectType.SPEED, 60, own(player) && OriginAbilities.inWoodland(player) ? 1 : 0);
    }
}
