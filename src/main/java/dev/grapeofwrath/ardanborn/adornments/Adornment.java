package dev.grapeofwrath.ardanborn.adornments;

import dev.grapeofwrath.ardanborn.armor.CustomItems;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/// The adornments: relics worn at the neck, on the back or as a belt. The same for every people.
/// The [#id()] is stored on items, so never rename a constant once released.
public enum Adornment {
    WHITE_GEM_OF_ARWEN(Slot.NECKLACE, "White Gem of Arwen", TextColor.color(0xEAF2FF), Material.QUARTZ,
            "Given in pity, against the memory of fear.",
            List.of("Darkness, Blindness and Weakness", "are sooner lifted from you",
                    "Beneath it the Shadow of a worn", "ring grows more slowly")),
    EMERALDS_OF_GIRION(Slot.NECKLACE, "Emeralds of Girion", TextColor.color(0x3FD06A), Material.EMERALD,
            "Green as grass, and a lord's ransom in Dale.",
            List.of("Villagers ask less of you", "At times emerald ore yields you more")),
    QUIVER_OF_LEGOLAS(Slot.BACK, "Quiver of Legolas", TextColor.color(0xA8D08A), Material.SPECTRAL_ARROW,
            "It came out of Lórien, and seldom was it empty.",
            List.of("Your arrows fly more swiftly", "At times an arrow is not spent")),
    SAMS_PACK(Slot.BACK, "Sam's Pack", TextColor.color(0xC99B62), Material.BROWN_BUNDLE,
            "Heavy with pots and pans, and borne gladly.",
            List.of("Food sustains you longer", "At times cooked food is not used up")),
    BELT_OF_BOROMIR(Slot.BELT, "Golden Belt of Boromir", TextColor.color(0xF0C24B), Material.GOLD_INGOT,
            "The Lady's gift to the son of Gondor.",
            List.of("When you are sorely hurt, you", "take less damage")),
    SHEATH_BELT_OF_ANDURIL(Slot.BELT, "Sheath-belt of Andúril", TextColor.color(0xCFE0F0), Material.LEAD,
            "The blade that is drawn from it shall not be stained.",
            List.of("Often your sword takes no wear"));

    public enum Slot {
        NECKLACE("Necklace"), BACK("Back"), BELT("Belt");

        private final String label;

        Slot(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }

        public String id() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    private final Slot slot;
    private final String displayName;
    private final TextColor color;
    private final Material look;
    private final String flavor;
    private final List<String> powers;

    Adornment(Slot slot, String displayName, TextColor color, Material look, String flavor, List<String> powers) {
        this.slot = slot;
        this.displayName = displayName;
        this.color = color;
        this.look = look;
        this.flavor = flavor;
        this.powers = powers;
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public Slot slot() {
        return slot;
    }

    public String displayName() {
        return displayName;
    }

    public ItemStack create() {
        // Nautilus shells do nothing of their own when used, burned or brewed.
        ItemStack item = ItemStack.of(Material.NAUTILUS_SHELL);
        item.setData(DataComponentTypes.ITEM_MODEL, look.getKey());
        item.setData(DataComponentTypes.ITEM_NAME, Component.text(displayName, color));
        item.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(id()).build());
        ItemLore.Builder lore = ItemLore.lore()
                .addLine(Component.text(flavor, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true))
                .addLine(Component.empty());
        for (String line : powers) lore.addLine(plain(line, NamedTextColor.BLUE));
        lore.addLine(Component.empty()).addLine(plain("Adornment: " + slot.label(), NamedTextColor.DARK_PURPLE));
        item.setData(DataComponentTypes.LORE, lore.build());
        CustomItems.tag(item, id());
        return item;
    }

    private static Component plain(String text, NamedTextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }

    /** The adornment an item is, or null. */
    public static @Nullable Adornment of(@Nullable ItemStack item) {
        String id = CustomItems.idOf(item);
        if (id == null) return null;
        for (Adornment adornment : values()) if (adornment.id().equals(id)) return adornment;
        return null;
    }

    /** `/ardanborn give` ids. */
    public static Map<String, Supplier<ItemStack>> giveableItems() {
        Map<String, Supplier<ItemStack>> items = new LinkedHashMap<>();
        for (Adornment adornment : values()) items.put(adornment.id(), adornment::create);
        return items;
    }
}
