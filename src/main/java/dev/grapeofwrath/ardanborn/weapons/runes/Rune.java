package dev.grapeofwrath.ardanborn.weapons.runes;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/// The Cirth runes a Smith can inscribe onto weapons and tools. Their effects live in [RuneEffects].
///
/// The [#id()] is stored on items, so never rename an enum constant once released.
public enum Rune {
    WESTERNESSE("Westernesse", TextColor.color(0xE8D9A8),
            "+30% damage to the undead",
            Material.BONE, Material.GOLD_NUGGET, Fits.MELEE),
    WARNING("Warning", TextColor.color(0x6FB8FF),
            "Glows blue when monsters are near",
            Material.GLOW_INK_SAC, Material.LAPIS_LAZULI, Fits.MELEE),
    FROST("Frost", TextColor.color(0xBFEFFF),
            "Hits slow the target",
            Material.BLUE_ICE, Material.SNOWBALL, Fits.FROST),
    DELVING("Delving", TextColor.color(0xD9A066),
            "Iron, copper and gold ore drop ingots",
            Material.COAL, Material.RAW_IRON, Fits.PICKAXE),
    HARVEST("Harvest", TextColor.color(0xD8C15A),
            "Right-click a ripe crop to reap and replant 3×3",
            Material.WHEAT, Material.BONE_MEAL, Fits.HOE),
    VEIN("Vein", TextColor.color(0xC75B4A),
            "Sneak to mine a whole ore vein (up to 16)",
            Material.REDSTONE, Material.IRON_INGOT, Fits.PICKAXE);

    private final String displayName;
    private final TextColor color;
    private final String effect;
    private final Material ingredientA;
    private final Material ingredientB;
    private final List<Tag<Material>> fits;

    Rune(String displayName, TextColor color, String effect, Material ingredientA, Material ingredientB,
         List<Tag<Material>> fits) {
        this.displayName = displayName;
        this.color = color;
        this.effect = effect;
        this.ingredientA = ingredientA;
        this.ingredientB = ingredientB;
        this.fits = fits;
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String displayName() {
        return displayName;
    }

    public TextColor color() {
        return color;
    }

    /** One line saying what the rune does, for lore. */
    public String effect() {
        return effect;
    }

    /** Placed at the corners of the rune-stone recipe. */
    public Material ingredientA() {
        return ingredientA;
    }

    /** Placed at the edges of the rune-stone recipe. */
    public Material ingredientB() {
        return ingredientB;
    }

    public boolean fits(Material material) {
        for (Tag<Material> tag : fits) {
            if (tag.isTagged(material)) return true;
        }
        return false;
    }

    public static @Nullable Rune byId(@Nullable String id) {
        if (id == null) return null;
        for (Rune rune : values()) {
            if (rune.id().equals(id)) return rune;
        }
        return null;
    }

    /** Which items each rune can go on. */
    private static final class Fits {
        static final List<Tag<Material>> MELEE = List.of(Tag.ITEMS_SWORDS, Tag.ITEMS_AXES, Tag.ITEMS_SPEARS,
                Tag.ITEMS_ENCHANTABLE_MACE, Tag.ITEMS_ENCHANTABLE_TRIDENT);
        static final List<Tag<Material>> FROST = List.of(Tag.ITEMS_SWORDS, Tag.ITEMS_AXES, Tag.ITEMS_SPEARS,
                Tag.ITEMS_ENCHANTABLE_MACE, Tag.ITEMS_ENCHANTABLE_TRIDENT, Tag.ITEMS_ENCHANTABLE_BOW,
                Tag.ITEMS_ENCHANTABLE_CROSSBOW);
        static final List<Tag<Material>> PICKAXE = List.of(Tag.ITEMS_PICKAXES);
        static final List<Tag<Material>> HOE = List.of(Tag.ITEMS_HOES);
    }
}
