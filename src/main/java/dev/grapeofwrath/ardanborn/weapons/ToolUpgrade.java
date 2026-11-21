package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.armor.CustomItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// A weapon or tool made at a smithing table from a race's template and a vanilla item, e.g. the Pearl of
/// Ossë and a trident make the Spear of the Havens. [ToolUpgrades] registers the recipe and builds the
/// result. Implementations that are also a [org.bukkit.event.Listener] are registered automatically.
///
/// Like every ArdanBorn race item, anyone gets the base effects and the origin it is made for gets extra.
public interface ToolUpgrade {

    /// Stable id stored on the result (and used by `/ardanborn give`). Never change it once released.
    String id();

    /// A fresh template item, used as the smithing recipe's example.
    ItemStack template();

    boolean isTemplate(@Nullable ItemStack item);

    /// The vanilla items this upgrade applies to.
    List<Material> baseMaterials();

    /// A copy of `base` made into this item. Enchantments, damage and any custom name are kept.
    ItemStack upgrade(ItemStack base);

    default boolean is(@Nullable ItemStack item) {
        return id().equals(CustomItems.idOf(item));
    }

    static Component lore(String text, NamedTextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }
}
