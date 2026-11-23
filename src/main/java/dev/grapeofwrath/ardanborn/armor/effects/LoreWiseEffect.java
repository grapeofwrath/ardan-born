package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.CustomArmor;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.armor.sets.GondolinSet;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Leggings: race items are ready again sooner. Origin and class powers aren't affected.
public final class LoreWiseEffect extends PieceEffect {
    public LoreWiseEffect(String setId) {
        super("lore_wise", "Lore-wise", setId, EquipmentSlot.LEGS, OriginCheck.NOLDOR);
    }

    @Override
    protected List<String> lines() {
        return List.of("Race items are ready sooner", "Noldor: sooner still");
    }

    /// A race item's cooldown for this player.
    public static long shorten(Player player, long ms) {
        if (!ItemPower.enabled("lore_wise")
                || CustomArmor.wornTier(player, EquipmentSlot.LEGS, GondolinSet.ID) == null) return ms;
        return Math.round(ms * (OriginCheck.is(player, OriginCheck.NOLDOR) ? 0.8 : 0.9));
    }
}
