package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Helmet: a meal does you good. Hobbits share the cheer.
public final class MerryHeartEffect extends PieceEffect implements Listener {
    public MerryHeartEffect(String setId) {
        super("merry_heart", "Merry Heart", setId, EquipmentSlot.HEAD, OriginCheck.HOBBIT);
    }

    @Override
    protected List<String> lines() {
        return List.of("Every meal brings Regeneration", "Hobbits: longer, and to all near");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (!event.getItem().getType().isEdible() || !worn(player)) return;
        if (!own(player)) {
            Effects.potion(player, PotionEffectType.REGENERATION, 80, 0);
            return;
        }
        for (Player each : Effects.company(player, 6)) Effects.potion(each, PotionEffectType.REGENERATION, 120, 0);
    }
}
