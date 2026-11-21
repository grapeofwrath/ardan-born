package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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

    @Override
    public String name() {
        return "Snowtread";
    }

    @Override
    public List<Component> describe(ArmorTier tier) {
        List<Component> lines = new ArrayList<>(List.of(
                Component.text("Walk on powder snow without sinking"),
                Component.text("Immune to freezing"),
                Component.text("Naugrim: step up full blocks")));
        if (tier == ArmorTier.NETHERITE) lines.add(Component.text("and move 10% faster on stone"));
        return lines;
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        boolean naugrim = OriginCheck.is(player, OriginCheck.NAUGRIM);
        set(player, Attribute.STEP_HEIGHT, STEP_KEY, naugrim ? STEP_BONUS : 0,
                AttributeModifier.Operation.ADD_NUMBER);
        boolean onStone = Tag.BASE_STONE_OVERWORLD.isTagged(
                player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType());
        set(player, Attribute.MOVEMENT_SPEED, SPEED_KEY,
                naugrim && tier == ArmorTier.NETHERITE && onStone ? STONE_SPEED_BONUS : 0,
                AttributeModifier.Operation.ADD_SCALAR);
    }

    @Override
    public void onUnequip(Player player) {
        remove(player, Attribute.STEP_HEIGHT, STEP_KEY);
        remove(player, Attribute.MOVEMENT_SPEED, SPEED_KEY);
    }

    /// Adds, changes or (for 0) removes a modifier. Transient, so it is never saved with the player and
    /// can't linger if the server stops while the boots are worn.
    private static void set(Player player, Attribute attribute, NamespacedKey key, double amount,
                            AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        AttributeModifier current = instance.getModifier(key);
        if (current != null && current.getAmount() == amount) return;
        if (current != null) instance.removeModifier(key);
        if (amount != 0) instance.addTransientModifier(new AttributeModifier(key, amount, operation));
    }

    private static void remove(Player player, Attribute attribute, NamespacedKey key) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) instance.removeModifier(key);
    }
}
