package dev.grapeofwrath.ardanborn.armor;

import dev.grapeofwrath.ardanborn.Keys;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/// Turning vanilla armor into set pieces, and reading set pieces back.
///
/// Effects are deliberately not stored as attribute modifiers on the item: setting that component
/// replaces the material's default armor values, which a later netherite upgrade would keep.
public final class CustomArmor {
    private CustomArmor() {}

    public static @Nullable ArmorSet setOf(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        String id = item.getPersistentDataContainer().get(Keys.ARMOR_SET, PersistentDataType.STRING);
        return id == null ? null : ArmorSets.byId(id);
    }

    public static boolean isSetPiece(@Nullable ItemStack item) {
        return setOf(item) != null;
    }

    /// Makes `item` a piece of `set` (in place) and returns it.
    public static ItemStack apply(ItemStack item, ArmorSet set) {
        item.editPersistentDataContainer(pdc -> pdc.set(Keys.ARMOR_SET, PersistentDataType.STRING, set.id()));
        // Lets a resource pack retexture set pieces later without code changes.
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(set.id()).build());
        return refresh(item);
    }

    /// Rewrites name, rarity and lore for the item's current material (e.g. after a netherite upgrade).
    public static ItemStack refresh(ItemStack item) {
        ArmorSet set = setOf(item);
        ArmorTier tier = ArmorTier.of(item.getType());
        EquipmentSlot slot = ArmorTier.slotOf(item.getType());
        if (set == null || tier == null || slot == null) return item;

        item.setData(DataComponentTypes.ITEM_NAME, Component.text(
                set.displayName() + " " + tier.displayName() + " " + ArmorTier.pieceName(slot), set.color()));
        item.setData(DataComponentTypes.RARITY, tier == ArmorTier.NETHERITE ? ItemRarity.EPIC : ItemRarity.RARE);
        item.setData(DataComponentTypes.LORE, ItemLore.lore(lore(set, tier, slot)));
        return item;
    }

    private static List<Component> lore(ArmorSet set, ArmorTier tier, EquipmentSlot slot) {
        List<Component> lines = new ArrayList<>();
        lines.add(line(set.displayName() + " Set", set.color()));
        addEffect(lines, set.pieceEffect(slot), tier, null);
        addEffect(lines, set.fullSetBonus(), tier, "Full set: ");
        return lines;
    }

    private static void addEffect(List<Component> lines, Optional<SetEffect> effect, ArmorTier tier, @Nullable String prefix) {
        effect.ifPresent(e -> {
            lines.add(Component.empty());
            lines.add(line((prefix == null ? "" : prefix) + e.name(), NamedTextColor.GOLD));
            for (Component description : e.describe(tier)) {
                lines.add(description.colorIfAbsent(NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            }
        });
    }

    static Component line(String text, net.kyori.adventure.text.format.TextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }
}
