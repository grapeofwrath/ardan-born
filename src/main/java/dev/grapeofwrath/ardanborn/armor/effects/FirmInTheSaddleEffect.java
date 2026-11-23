package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.mounts.Riding;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Leggings: in the saddle, melee attacks recover faster.
public final class FirmInTheSaddleEffect extends PieceEffect {
    private static final NamespacedKey KEY = Keys.key("firm_in_the_saddle");

    public FirmInTheSaddleEffect(String setId) {
        super("firm_in_the_saddle", "Firm in the Saddle", setId, EquipmentSlot.LEGS, OriginCheck.ROHIRRIM);
    }

    @Override
    protected List<String> lines() {
        return List.of("Riding, you strike the faster", "Rohirrim: faster still");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        Effects.scale(player, Attribute.ATTACK_SPEED, KEY, Riding.mounted(player) ? (own(player) ? 0.25 : 0.15) : 0);
    }

    @Override
    public void onUnequip(Player player) {
        Effects.scale(player, Attribute.ATTACK_SPEED, KEY, 0);
    }
}
