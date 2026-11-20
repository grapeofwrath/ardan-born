package dev.grapeofwrath.ardanborn.armor;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/// The vanilla armor materials a set can be applied to. Each set therefore comes in two variants:
/// diamond, and the stronger netherite.
///
/// The tier is always read from the item's current material and never stored, so a vanilla
/// netherite upgrade of a diamond set piece automatically makes it the netherite variant.
public enum ArmorTier {
    DIAMOND("Diamond"),
    NETHERITE("Netherite");

    private final String displayName;

    ArmorTier(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    /// The tier of an armor item, or null if it isn't a diamond or netherite armor piece.
    public static @Nullable ArmorTier of(@Nullable Material material) {
        if (material == null || slotOf(material) == null) return null;
        String name = material.name();
        for (ArmorTier tier : values()) {
            if (name.startsWith(tier.name() + "_")) return tier;
        }
        return null;
    }

    /// The vanilla armor item of this tier for a slot, e.g. DIAMOND + FEET -> DIAMOND_BOOTS.
    public Material material(EquipmentSlot slot) {
        String piece = switch (slot) {
            case HEAD -> "HELMET";
            case CHEST -> "CHESTPLATE";
            case LEGS -> "LEGGINGS";
            case FEET -> "BOOTS";
            default -> throw new IllegalArgumentException("Not an armor slot: " + slot);
        };
        return Material.valueOf(name() + "_" + piece);
    }

    /// The slot an armor item is worn in, or null if it isn't a helmet/chestplate/leggings/boots.
    public static @Nullable EquipmentSlot slotOf(@Nullable Material material) {
        if (material == null) return null;
        String name = material.name();
        if (name.endsWith("_HELMET")) return EquipmentSlot.HEAD;
        if (name.endsWith("_CHESTPLATE")) return EquipmentSlot.CHEST;
        if (name.endsWith("_LEGGINGS")) return EquipmentSlot.LEGS;
        if (name.endsWith("_BOOTS")) return EquipmentSlot.FEET;
        return null;
    }

    /// Every armor material that can become a set piece.
    public static List<Material> armorMaterials() {
        List<Material> result = new ArrayList<>();
        for (Material material : Material.values()) {
            if (!material.isLegacy() && material.isItem() && of(material) != null) result.add(material);
        }
        return result;
    }

    public static String pieceName(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> "Helmet";
            case CHEST -> "Chestplate";
            case LEGS -> "Leggings";
            case FEET -> "Boots";
            default -> slot.name();
        };
    }
}
