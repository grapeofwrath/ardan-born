package dev.grapeofwrath.ardanborn.armor;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/// A custom armor set: a template item that, combined with a diamond or netherite armor piece at a smithing
/// table, turns it into a piece of this set. Pieces keep their vanilla material, so they enchant,
/// trim, repair and netherite-upgrade like normal armor.
public interface ArmorSet {

    /// Stable id stored on items. Never change it once released.
    String id();

    /// Used in item names, e.g. "Arkenstone" -> "Arkenstone Iron Boots".
    String displayName();

    TextColor color();

    /// A fresh template item (the smithing-table ingredient).
    ItemStack createTemplate();

    default boolean isTemplate(ItemStack item) {
        return id().equals(CustomItems.idOf(item));
    }

    Optional<SetEffect> pieceEffect(EquipmentSlot slot);

    /// The item a piece in this slot is turned into, if not the input armor itself. The piece keeps the
    /// input's tier (diamond or netherite) and gets that tier's stats. Used where vanilla ties a behavior
    /// to a specific item, e.g. leather boots for walking on powder snow.
    default Optional<Material> baseMaterial(EquipmentSlot slot) {
        return Optional.empty();
    }

    /// Default dye, per tier, for pieces whose base material is dyeable (leather). Players can re-dye them.
    default Optional<Color> dyeColor(ArmorTier tier) {
        return Optional.empty();
    }

    /// Bonus for wearing all four pieces. It is the netherite variant only if all four are netherite.
    default Optional<SetEffect> fullSetBonus() {
        return Optional.empty();
    }

    /// Register crafting recipes for the template. The smithing recipe is added by [ArmorSets].
    default void registerRecipes(Plugin plugin) {}
}
