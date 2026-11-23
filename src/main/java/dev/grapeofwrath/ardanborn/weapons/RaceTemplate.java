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
import org.bukkit.Tag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// A race's smithing template, for the races added after the Arkenstone, Pearl, Feather and Honey (which
/// predate this helper and keep their own classes). Built on prismarine crystals with another item's look,
/// so vanilla recipes can't use it; RecipeGuardListener keeps it out of prismarine recipes.
///
/// Every template uses the shared recipe shape `UVU / GDG / UVU`: a diamond in the center, gold ingots
/// either side, and two race ingredients.
public record RaceTemplate(String id, String name, TextColor color, Material look, String flavor,
                           List<String> usage, RecipeChoice u, RecipeChoice v) {

    public static final RaceTemplate LIGHT_OF_THE_TREES = new RaceTemplate("light_of_the_trees",
            "Light of the Trees", TextColor.color(0xFFF1B0), Material.GLOWSTONE_DUST,
            "Gold and silver were mingled in it.",
            List.of("Wed it at a Smithing Table to diamond or", "netherite armor, to a sword or a glass bottle."),
            new RecipeChoice.MaterialChoice(Material.GLOWSTONE_DUST),
            new RecipeChoice.MaterialChoice(Material.GLOW_BERRIES));

    public static final RaceTemplate MALLORN_LEAF = new RaceTemplate("mallorn_leaf",
            "Mallorn Leaf", TextColor.color(0xE3C35A), Material.LILY_PAD,
            "Though it has fallen it does not fade.",
            List.of("Wed it at a Smithing Table to diamond or", "netherite armor, to a bow or a lead."),
            new RecipeChoice.MaterialChoice(Tag.LEAVES),
            new RecipeChoice.MaterialChoice(Material.STRING));

    public static final RaceTemplate STAR_OF_ELENDIL = new RaceTemplate("star_of_elendil",
            "Star of Elendil", TextColor.color(0xDCE6F5), Material.NETHER_STAR,
            "Kings wore it who are long under mound.",
            List.of("Wed it at a Smithing Table to diamond or", "netherite armor, to a sword or a goat horn."),
            new RecipeChoice.MaterialChoice(Material.QUARTZ),
            new RecipeChoice.MaterialChoice(Material.IRON_INGOT));

    public static final RaceTemplate EVENSTAR = new RaceTemplate("evenstar",
            "Evenstar", TextColor.color(0xC9D8FF), Material.HEART_OF_THE_SEA,
            "It was given in love, and not asked for again.",
            List.of("Wed it at a Smithing Table to diamond or", "netherite armor, to a sword or a honey bottle."),
            new RecipeChoice.MaterialChoice(Material.ENDER_PEARL),
            new RecipeChoice.MaterialChoice(Material.GLOW_INK_SAC));

    public static final RaceTemplate MATHOM = new RaceTemplate("mathom",
            "Mathom", TextColor.color(0x8DB255), Material.BUNDLE,
            "Long in a drawer, and the giver forgotten.",
            List.of("Wed it at a Smithing Table to diamond or", "netherite armor, to a sword or a stick."),
            new RecipeChoice.MaterialChoice(Material.PUMPKIN_PIE),
            new RecipeChoice.MaterialChoice(Material.IRON_INGOT));

    public static final RaceTemplate WHITE_HAND = new RaceTemplate("white_hand",
            "White Hand", TextColor.color(0xF2F2F2), Material.WHITE_DYE,
            "Those who bore it were promised much.",
            List.of("Wed it at a Smithing Table to diamond or", "netherite armor, to a sword or a glass bottle."),
            new RecipeChoice.MaterialChoice(Material.COAL_BLOCK),
            new RecipeChoice.MaterialChoice(Material.WHITE_DYE));

    public static final RaceTemplate BANNER_OF_THE_MARK = new RaceTemplate("banner_of_the_mark",
            "Banner of the Mark", TextColor.color(0x5FAE5B), Material.GREEN_BANNER,
            "White upon green, the horse runs free.",
            List.of("Wed it at a Smithing Table to diamond or", "netherite armor, horse armor or a spear."),
            new RecipeChoice.MaterialChoice(Material.HAY_BLOCK),
            new RecipeChoice.MaterialChoice(Material.LEATHER));

    public ItemStack create() {
        ItemStack item = ItemStack.of(Material.PRISMARINE_CRYSTALS);
        item.setData(DataComponentTypes.ITEM_MODEL, look.getKey());
        item.setData(DataComponentTypes.ITEM_NAME, Component.text(name, color));
        item.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(id).build());
        ItemLore.Builder lore = ItemLore.lore()
                .addLine(Component.text(flavor, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true))
                .addLine(Component.empty());
        for (String line : usage) lore.addLine(ToolUpgrade.lore(line, NamedTextColor.BLUE));
        item.setData(DataComponentTypes.LORE, lore.build());
        CustomItems.tag(item, id);
        return item;
    }

    public boolean is(@Nullable ItemStack item) {
        return id.equals(CustomItems.idOf(item));
    }

    public void registerRecipe() {
        NamespacedKey key = Keys.key(id);
        ShapedRecipe recipe = new ShapedRecipe(key, create());
        recipe.shape("UVU", "GDG", "UVU");
        recipe.setIngredient('U', u);
        recipe.setIngredient('V', v);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('D', Material.DIAMOND);
        Bukkit.addRecipe(recipe);
        ArmorSets.trackRecipe(key);
    }
}
