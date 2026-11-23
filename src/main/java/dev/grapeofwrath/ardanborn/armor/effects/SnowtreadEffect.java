package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

/// Boots: walk on powder snow without sinking, and never freeze. Naugrim also get Mountain Stride: they
/// step up full blocks, and on netherite move faster over natural stone.
///
/// Vanilla grants Snowtread, on server and client alike, only to boots that are `minecraft:leather_boots`
/// (freezing immunity via the `freeze_immune_wearables` tag). So that part needs no code: sets that use
/// it make those boots leather-based with diamond or netherite stats (see
/// [dev.grapeofwrath.ardanborn.armor.ArmorSet#baseMaterial]).
public final class SnowtreadEffect implements SetEffect {
    private static final NamespacedKey STEP_KEY = Keys.key("mountain_stride_step");
    private static final NamespacedKey SPEED_KEY = Keys.key("mountain_stride_speed");
    /// Vanilla step height is 0.6; this lets the wearer walk up a full block.
    private static final double STEP_BONUS = 0.5;
    private static final double STONE_SPEED_BONUS = 0.10;

    private static final String EFFECT_ID = "snowtread";

    @Override
    public String id() {
        return EFFECT_ID;
    }

    @Override
    public String name() {
        return "Snowtread";
    }

    @Override
    public List<Component> describe(ArmorTier tier) {
        return List.of(
                Component.text("You walk upon powder snow, nor ever freeze"),
                Component.text("Naugrim: and step up full blocks"));
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        boolean naugrim = OriginCheck.is(player, OriginCheck.NAUGRIM);
        Effects.modifier(player, Attribute.STEP_HEIGHT, STEP_KEY, naugrim ? STEP_BONUS : 0,
                AttributeModifier.Operation.ADD_NUMBER);
        boolean onStone = Tag.BASE_STONE_OVERWORLD.isTagged(
                player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType());
        Effects.modifier(player, Attribute.MOVEMENT_SPEED, SPEED_KEY,
                naugrim && tier == ArmorTier.NETHERITE && onStone ? STONE_SPEED_BONUS : 0,
                AttributeModifier.Operation.ADD_SCALAR);
    }

    @Override
    public void onUnequip(Player player) {
        Effects.modifier(player, Attribute.STEP_HEIGHT, STEP_KEY, 0, AttributeModifier.Operation.ADD_NUMBER);
        Effects.scale(player, Attribute.MOVEMENT_SPEED, SPEED_KEY, 0);
    }
}
