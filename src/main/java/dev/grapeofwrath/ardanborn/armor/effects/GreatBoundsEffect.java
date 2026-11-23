package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Leggings: leap higher, and for Beornings higher still at night.
public final class GreatBoundsEffect extends PieceEffect {
    private static final NamespacedKey KEY = Keys.key("great_bounds");
    // Height goes with the square of the jump's speed: these are the square roots of 1.2 and 1.35, less 1.
    private static final double HIGHER = 0.095;
    private static final double NIGHT = 0.162;

    public GreatBoundsEffect(String setId) {
        super("great_bounds", "Great Bounds", setId, EquipmentSlot.LEGS, OriginCheck.BEORNING);
    }

    @Override
    protected List<String> lines() {
        return List.of("Jump higher", "Beornings: higher still at night");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        Effects.scale(player, Attribute.JUMP_STRENGTH, KEY,
                OriginAbilities.isNight(player.getWorld()) && own(player) ? NIGHT : HIGHER);
    }

    @Override
    public void onUnequip(Player player) {
        Effects.scale(player, Attribute.JUMP_STRENGTH, KEY, 0);
    }
}
