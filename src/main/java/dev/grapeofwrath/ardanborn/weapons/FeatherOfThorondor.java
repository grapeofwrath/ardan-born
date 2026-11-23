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
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

/// The Eagles' template, as the Arkenstone is the dwarves' and the Pearl of Ossë the Teleri's. It makes
/// the Thorondor armor set (see [dev.grapeofwrath.ardanborn.armor.sets.ThorondorSet]) and [Aeglos].
public final class FeatherOfThorondor {
    public static final String ID = "feather_of_thorondor";
    public static final TextColor COLOR = TextColor.color(0xE8C98A);

    private FeatherOfThorondor() {}

    public static ItemStack create() {
        // Looks like a feather, but is built on prismarine crystals so it can't be used in vanilla recipes.
        // RecipeGuardListener keeps it out of prismarine recipes.
        ItemStack item = ItemStack.of(Material.PRISMARINE_CRYSTALS);
        item.setData(DataComponentTypes.ITEM_MODEL, Material.FEATHER.getKey());
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Feather of Thorondor", COLOR));
        item.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(ID).build());
        item.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(Component.text("Shed by the King of Eagles.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true))
                .addLine(Component.empty())
                .addLine(plain("Combine with diamond or netherite"))
                .addLine(plain("armor, a spear or a breeze rod at a Smithing Table."))
                .build());
        CustomItems.tag(item, ID);
        return item;
    }

    public static boolean is(@Nullable ItemStack item) {
        return ID.equals(CustomItems.idOf(item));
    }

    public static void registerRecipe() {
        NamespacedKey key = Keys.key(ID);
        ShapedRecipe recipe = new ShapedRecipe(key, create());
        recipe.shape("FBF", "GDG", "FBF");
        recipe.setIngredient('F', Material.FEATHER);
        recipe.setIngredient('B', Material.BREEZE_ROD);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('D', Material.DIAMOND);
        Bukkit.addRecipe(recipe);
        ArmorSets.trackRecipe(key);
    }

    private static Component plain(String text) {
        return Component.text(text, NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false);
    }
}
