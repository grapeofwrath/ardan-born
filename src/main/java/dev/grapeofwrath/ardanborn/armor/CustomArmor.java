package dev.grapeofwrath.ardanborn.armor;

import dev.grapeofwrath.ardanborn.Keys;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
/// Set effects are deliberately not stored as attribute modifiers on the item: setting that component
/// replaces the material's default armor values, which a later netherite upgrade would keep. The one
/// exception is pieces on a different base material ([ArmorSet#baseMaterial]): they carry their tier's
/// vanilla stats, and our own netherite upgrade ([ArmorSmithingListener]) rewrites them.
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

    /// The tier of the piece of set `setId` the player wears in `slot`, or null if they don't wear one.
    /// For effects that react to events rather than ticking.
    public static @Nullable ArmorTier wornTier(Player player, EquipmentSlot slot, String setId) {
        ItemStack item = player.getInventory().getItem(slot);
        ArmorSet set = setOf(item);
        if (set == null || !set.id().equals(setId) || ArmorTier.slotOf(item) != slot) return null;
        return tierOf(item);
    }

    /// A set piece on a different base material (e.g. leather-based boots), which vanilla smithing
    /// recipes don't treat as diamond or netherite.
    public static boolean isConverted(@Nullable ItemStack item) {
        return isSetPiece(item) && item.getPersistentDataContainer().has(Keys.ARMOR_TIER, PersistentDataType.STRING);
    }

    /// The piece's tier: stored on pieces whose material isn't diamond or netherite (see
    /// [ArmorSet#baseMaterial]), otherwise read from the material.
    public static @Nullable ArmorTier tierOf(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        String stored = item.getPersistentDataContainer().get(Keys.ARMOR_TIER, PersistentDataType.STRING);
        if (stored != null) {
            try {
                return ArmorTier.valueOf(stored);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return ArmorTier.of(item.getType());
    }

    /// Makes `item` (a diamond or netherite armor piece) a piece of `set` and returns it. This is a new
    /// stack if the set uses a different base material for this slot, otherwise `item` itself.
    public static ItemStack apply(ItemStack item, ArmorSet set) {
        ArmorTier tier = ArmorTier.of(item.getType());
        EquipmentSlot slot = ArmorTier.slotOf(item.getType());
        Material base = slot == null ? null : set.baseMaterial(slot).orElse(null);
        if (tier != null && base != null && base != item.getType()) {
            // Keeps enchantments, trim, damage and custom name.
            item = item.withType(base);
            applyTierStats(item, tier, slot);
            if (!item.hasData(DataComponentTypes.DYED_COLOR)) {
                setDye(item, set.dyeColor(tier).orElse(null));
            }
        }
        item.editPersistentDataContainer(pdc -> pdc.set(Keys.ARMOR_SET, PersistentDataType.STRING, set.id()));
        // Lets a resource pack retexture set pieces later without code changes.
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(set.id()).build());
        return refresh(item);
    }

    /// Gives a piece whose material isn't its tier (e.g. leather boots) that tier's stats, copied from the
    /// vanilla diamond or netherite item for the slot, and records the tier.
    public static void applyTierStats(ItemStack item, ArmorTier tier, EquipmentSlot slot) {
        Material vanilla = tier.material(slot);
        item.editPersistentDataContainer(pdc -> pdc.set(Keys.ARMOR_TIER, PersistentDataType.STRING, tier.name()));
        copyDefault(item, vanilla, DataComponentTypes.ATTRIBUTE_MODIFIERS);
        copyDefault(item, vanilla, DataComponentTypes.MAX_DAMAGE);
        copyDefault(item, vanilla, DataComponentTypes.ENCHANTABLE);
        copyDefault(item, vanilla, DataComponentTypes.REPAIRABLE);
        copyDefault(item, vanilla, DataComponentTypes.DAMAGE_RESISTANT);
        // A base that isn't worn in this slot (a spyglass helmet) is worn and shown like the vanilla piece.
        // Its icon is the vanilla piece too; in hand it still works as its base (the spyglass still zooms).
        Equippable own = item.getType().getDefaultData(DataComponentTypes.EQUIPPABLE);
        if (own == null || own.slot() != slot) {
            copyDefault(item, vanilla, DataComponentTypes.EQUIPPABLE);
            item.setData(DataComponentTypes.ITEM_MODEL, vanilla.getKey());
        }
    }

    /// Moves a piece from one tier's default dye to another's, e.g. brown to gold on a netherite upgrade.
    /// A color the player chose themselves is kept.
    public static void updateDefaultDye(ItemStack item, ArmorSet set, ArmorTier from, ArmorTier to) {
        DyedItemColor current = item.getData(DataComponentTypes.DYED_COLOR);
        Color oldDefault = set.dyeColor(from).orElse(null);
        if (current == null || current.color().equals(oldDefault)) {
            setDye(item, set.dyeColor(to).orElse(null));
        }
    }

    private static void setDye(ItemStack item, @Nullable Color color) {
        if (color != null) item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(color));
    }

    private static <T> void copyDefault(ItemStack item, Material from, DataComponentType.Valued<T> type) {
        T value = from.getDefaultData(type);
        if (value != null) item.setData(type, value);
        else item.unsetData(type);
    }

    /// Rewrites name, rarity and lore for the item's current material (e.g. after a netherite upgrade).
    public static ItemStack refresh(ItemStack item) {
        ArmorSet set = setOf(item);
        ArmorTier tier = tierOf(item);
        EquipmentSlot slot = ArmorTier.slotOf(item);
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
