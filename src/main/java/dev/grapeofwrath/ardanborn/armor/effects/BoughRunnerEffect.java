package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Boots: step up full blocks. Sindar leap higher in woodland.
public final class BoughRunnerEffect extends PieceEffect {
    private static final NamespacedKey KEY = Keys.key("bough_runner");

    public BoughRunnerEffect(String setId) {
        super("bough_runner", "Bough-runner", setId, EquipmentSlot.FEET, OriginCheck.SINDAR);
    }

    @Override
    protected List<String> lines() {
        return List.of("You step up full blocks", "Sindar: in woodland you leap higher");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        // Vanilla step height is 0.6.
        Effects.modifier(player, Attribute.STEP_HEIGHT, KEY, 0.5, AttributeModifier.Operation.ADD_NUMBER);
        if (every(20) && OriginAbilities.inWoodland(player) && own(player)) {
            Effects.potion(player, PotionEffectType.JUMP_BOOST, 40, 0);
        }
    }

    @Override
    public void onUnequip(Player player) {
        Effects.modifier(player, Attribute.STEP_HEIGHT, KEY, 0, AttributeModifier.Operation.ADD_NUMBER);
    }
}
