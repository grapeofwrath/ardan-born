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

/// The Beornings' template, as the Arkenstone is the dwarves' and the Feather of Thorondor the Eagles'.
/// It makes the [HuntingBow].
public final class HoneyOfBeorn {
    public static final String ID = "honey_of_beorn";
    public static final TextColor COLOR = TextColor.color(0xF2B23A);

    private HoneyOfBeorn() {}

    public static ItemStack create() {
        // Looks like honeycomb, but is built on prismarine crystals so it can't be used in vanilla recipes.
        // RecipeGuardListener keeps it out of prismarine recipes.
        ItemStack item = ItemStack.of(Material.PRISMARINE_CRYSTALS);
        item.setData(DataComponentTypes.ITEM_MODEL, Material.HONEYCOMB.getKey());
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Honey of Beorn", COLOR));
        item.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(ID).build());
        item.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(Component.text("From the hives of Beorn's hall.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Combine with a bow", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("at a Smithing Table.", NamedTextColor.BLUE))
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
        recipe.shape("HKH", "GDG", "HKH");
        recipe.setIngredient('H', Material.HONEYCOMB);
        recipe.setIngredient('K', Material.HONEY_BLOCK);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('D', Material.DIAMOND);
        Bukkit.addRecipe(recipe);
        ArmorSets.trackRecipe(key);
    }
}
