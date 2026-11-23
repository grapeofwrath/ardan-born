package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Helmet: no Weakness. Edain shake off harmful effects sooner. Effects from plugins, such as the Morgul wound's, aren't changed.
public final class SteadfastEffect extends PieceEffect implements Listener {
    public SteadfastEffect(String setId) {
        super("steadfast", "Steadfast", setId, EquipmentSlot.HEAD, OriginCheck.EDAIN);
    }

    @Override
    protected List<String> lines() {
        return List.of("Weakness touches you not", "Edain: other ill effects pass sooner");
    }

    @EventHandler(ignoreCancelled = true)
    public void onEffect(EntityPotionEffectEvent event) {
        PotionEffect added = event.getNewEffect();
        if (added == null || event.getCause() == EntityPotionEffectEvent.Cause.PLUGIN) return;
        if (added.getType().getEffectCategory() != PotionEffectType.Category.HARMFUL) return;
        if (!(event.getEntity() instanceof Player player) || !worn(player)) return;
        if (added.getType() == PotionEffectType.WEAKNESS) {
            event.setCancelled(true);
        } else if (own(player) && !added.isInfinite() && added.getDuration() >= 20) {
            event.setCancelled(true);
            // Cause PLUGIN, so it doesn't come back here.
            player.addPotionEffect(added.withDuration(added.getDuration() * 3 / 4));
        }
    }
}
