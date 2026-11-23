package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorSet;
import dev.grapeofwrath.ardanborn.armor.ArmorSets;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.armor.effects.DurinsWealthEffect;
import dev.grapeofwrath.ardanborn.armor.effects.DwarfMarchEffect;
import dev.grapeofwrath.ardanborn.armor.effects.OreSenseEffect;
import dev.grapeofwrath.ardanborn.armor.effects.SnowtreadEffect;
import dev.grapeofwrath.ardanborn.armor.effects.UnmovedEffect;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/// The Heart of the Mountain: a dwarven armor set made to complement the Naugrim origin.
public final class ArkenstoneSet implements ArmorSet {
    public static final String ID = "arkenstone";
    private static final TextColor COLOR = TextColor.color(0xD8ECFF);
    /// Default dye of the leather-based boots: a dark brown between brown and black for diamond, and
    /// gold for netherite.
    private static final Color DIAMOND_BOOTS_DYE = Color.fromRGB(0x3B2A20);
    private static final Color NETHERITE_BOOTS_DYE = Color.fromRGB(0xFFD83D);

    private final SnowtreadEffect snowtread = new SnowtreadEffect();
    private final OreSenseEffect oreSense = new OreSenseEffect(ID);
    private final UnmovedEffect unmoved = new UnmovedEffect(ID);
    private final DwarfMarchEffect dwarfMarch = new DwarfMarchEffect(ID);
    private final DurinsWealthEffect durinsWealth = new DurinsWealthEffect(ID);

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String displayName() {
        return "Arkenstone";
    }

    @Override
    public TextColor color() {
        return COLOR;
    }

    @Override
    public ItemStack createTemplate() {
        return template();
    }

    /// The Arkenstone. It makes the armor set, and the Arkenstone pickaxe (see weapons.ArkenstonePickaxe).
    public static ItemStack template() {
        // Looks like raw gold, but is built on prismarine crystals: raw gold itself could be smelted
        // or used in vanilla recipes. RecipeGuardListener keeps it out of prismarine recipes.
        ItemStack item = ItemStack.of(Material.PRISMARINE_CRYSTALS);
        item.setData(DataComponentTypes.ITEM_MODEL, Material.RAW_GOLD.getKey());
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Arkenstone", COLOR));
        item.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(ID).build());
        item.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(Component.text("The Heart of the Mountain.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true))
                .addLine(Component.empty())
                .addLine(Component.text("Combine with diamond or netherite", NamedTextColor.BLUE)
                        .decoration(TextDecoration.ITALIC, false))
                .addLine(Component.text("armor, a pickaxe or an axe at a Smithing Table.", NamedTextColor.BLUE)
                        .decoration(TextDecoration.ITALIC, false))
                .build());
        CustomItems.tag(item, ID);
        return item;
    }

    @Override
    public void registerRecipes(Plugin plugin) {
        NamespacedKey key = Keys.key(ID);
        ShapedRecipe recipe = new ShapedRecipe(key, createTemplate());
        // Like every race's template: a diamond in the center, gold either side, and two ingredients of its own.
        recipe.shape("AEA", "GDG", "AEA");
        recipe.setIngredient('A', Material.AMETHYST_SHARD);
        recipe.setIngredient('E', Material.EMERALD);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('D', Material.DIAMOND);
        Bukkit.addRecipe(recipe);
        ArmorSets.trackRecipe(key);
    }

    /// The boots are leather boots with diamond or netherite stats: vanilla lets only leather boots walk on
    /// powder snow (on both server and client), which is what Snowtread is.
    @Override
    public Optional<Material> baseMaterial(EquipmentSlot slot) {
        return slot == EquipmentSlot.FEET ? Optional.of(Material.LEATHER_BOOTS) : Optional.empty();
    }

    @Override
    public Optional<Color> dyeColor(ArmorTier tier) {
        return Optional.of(tier == ArmorTier.NETHERITE ? NETHERITE_BOOTS_DYE : DIAMOND_BOOTS_DYE);
    }

    @Override
    public Optional<SetEffect> pieceEffect(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> Optional.of(oreSense);
            case CHEST -> Optional.of(unmoved);
            case LEGS -> Optional.of(dwarfMarch);
            case FEET -> Optional.of(snowtread);
            default -> Optional.empty();
        };
    }

    @Override
    public Optional<SetEffect> fullSetBonus() {
        return Optional.of(durinsWealth);
    }
}
