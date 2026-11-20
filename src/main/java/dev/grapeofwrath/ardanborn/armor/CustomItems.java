package dev.grapeofwrath.ardanborn.armor;

import dev.grapeofwrath.ardanborn.Keys;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/// Identifies plugin-made items (templates and the like) by their persistent data, not their looks,
/// so changing an item's name or lore later never breaks existing items.
public final class CustomItems {
    private CustomItems() {}

    public static @Nullable String idOf(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        return item.getPersistentDataContainer().get(Keys.ITEM, PersistentDataType.STRING);
    }

    public static boolean isCustom(@Nullable ItemStack item) {
        return idOf(item) != null;
    }

    public static void tag(ItemStack item, String id) {
        item.editPersistentDataContainer(pdc -> pdc.set(Keys.ITEM, PersistentDataType.STRING, id));
    }
}
