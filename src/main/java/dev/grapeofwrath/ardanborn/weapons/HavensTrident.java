package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorSets;
import dev.grapeofwrath.ardanborn.armor.CustomItems;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/// The Teleri's item, as the Arkenstone is the Naugrim's: the Pearl of Ossë, combined with a trident at a
/// smithing table, makes the Spear of the Havens. Its effects are in [HavensTridentListener].
///
/// Like every ArdanBorn weapon or tool, anyone gets the base effects and the origin it is made for
/// (here the Teleri) gets extra.
public final class HavensTrident {
    public static final String TEMPLATE_ID = "pearl_of_osse";
    public static final String SPEAR_ID = "spear_of_the_havens";
    /// Extra damage for Teleri wielders.
    public static final double TELERI_BONUS = 1.15;
    private static final TextColor COLOR = TextColor.color(0x7FD8E6);

    private HavensTrident() {}

    public static ItemStack createTemplate() {
        // Looks like a heart of the sea, but is built on prismarine crystals so it can't be used in a
        // conduit recipe. RecipeGuardListener keeps it out of prismarine recipes.
        ItemStack item = ItemStack.of(Material.PRISMARINE_CRYSTALS);
        item.setData(DataComponentTypes.ITEM_MODEL, Material.HEART_OF_THE_SEA.getKey());
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Pearl of Ossë", COLOR));
        item.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(TEMPLATE_ID).build());
        item.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(Component.text("A pearl of the shores of Alqualondë,", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true))
                .addLine(Component.text("gift of Ossë.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true))
                .addLine(Component.empty())
                .addLine(plain("Combine with a trident", NamedTextColor.BLUE))
                .addLine(plain("at a Smithing Table.", NamedTextColor.BLUE))
                .build());
        CustomItems.tag(item, TEMPLATE_ID);
        return item;
    }

    public static boolean isTemplate(@Nullable ItemStack item) {
        return TEMPLATE_ID.equals(CustomItems.idOf(item));
    }

    public static boolean isSpear(@Nullable ItemStack item) {
        return SPEAR_ID.equals(CustomItems.idOf(item));
    }

    /// A copy of the trident made into the Spear of the Havens. Enchantments, damage and any custom name
    /// are kept. Being tagged also keeps it out of the crafting grid's repair recipe, which would strip it.
    public static ItemStack upgrade(ItemStack trident) {
        ItemStack spear = trident.clone();
        spear.setData(DataComponentTypes.ITEM_NAME, Component.text("Spear of the Havens", COLOR));
        spear.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        spear.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(SPEAR_ID).build());
        spear.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(plain("Always returns when thrown", NamedTextColor.BLUE))
                .addLine(plain("Channeling calls lightning on", NamedTextColor.BLUE))
                .addLine(plain("foes in water or rain", NamedTextColor.BLUE))
                .addLine(Component.empty())
                .addLine(plain("Teleri: +15% damage", NamedTextColor.DARK_AQUA))
                .build());
        CustomItems.tag(spear, SPEAR_ID);
        return spear;
    }

    public static void registerRecipes() {
        NamespacedKey craft = Keys.key(TEMPLATE_ID);
        ShapedRecipe recipe = new ShapedRecipe(craft, createTemplate());
        recipe.shape("NPN", "PDP", "NPN");
        recipe.setIngredient('N', Material.NAUTILUS_SHELL);
        recipe.setIngredient('P', Material.PRISMARINE_SHARD);
        recipe.setIngredient('D', Material.DIAMOND);
        Bukkit.addRecipe(recipe);
        ArmorSets.trackRecipe(craft);

        // Template + trident + empty addition slot. The real result is built in HavensTridentListener;
        // this recipe only makes the smithing table accept the inputs.
        NamespacedKey smithing = Keys.key(SPEAR_ID + "_smithing");
        Bukkit.addRecipe(new SmithingTransformRecipe(smithing, new ItemStack(Material.TRIDENT),
                RecipeChoice.predicateChoice(HavensTrident::isTemplate, createTemplate()),
                new RecipeChoice.MaterialChoice(Material.TRIDENT),
                RecipeChoice.empty(),
                false));
        ArmorSets.trackRecipe(smithing);
    }

    public static Map<String, Supplier<ItemStack>> giveableItems() {
        Map<String, Supplier<ItemStack>> items = new LinkedHashMap<>();
        items.put(TEMPLATE_ID, HavensTrident::createTemplate);
        items.put(SPEAR_ID, () -> upgrade(ItemStack.of(Material.TRIDENT)));
        return items;
    }

    private static Component plain(String text, NamedTextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }
}
