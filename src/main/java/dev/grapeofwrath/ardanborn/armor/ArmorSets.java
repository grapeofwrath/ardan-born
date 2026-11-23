package dev.grapeofwrath.ardanborn.armor;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.sets.AlqualondeSet;
import dev.grapeofwrath.ardanborn.armor.sets.ArkenstoneSet;
import dev.grapeofwrath.ardanborn.armor.sets.LorienSet;
import dev.grapeofwrath.ardanborn.armor.sets.ThorondorSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/// Registry of all armor sets. Add new sets in [#registerAll].
public final class ArmorSets {
    private static final Map<String, ArmorSet> SETS = new LinkedHashMap<>();
    private static final List<NamespacedKey> RECIPES = new ArrayList<>();
    private static final List<EquipmentSlot> ARMOR_SLOTS =
            List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    private ArmorSets() {}

    public static void registerAll(Plugin plugin) {
        register(plugin, new ArkenstoneSet());
        register(plugin, new AlqualondeSet());
        register(plugin, new ThorondorSet());
        register(plugin, new LorienSet());
        registerNetheriteUpgrades();
    }

    /// Vanilla's netherite upgrade needs a real diamond piece as its base, so pieces on another base
    /// material (leather boots, turtle shell, spyglass) get their own recipe, one per base material: the
    /// example item is what makes the smithing table accept that material. The result is built in
    /// [ArmorSmithingListener].
    private static void registerNetheriteUpgrades() {
        Set<Material> bases = new LinkedHashSet<>();
        for (ArmorSet set : SETS.values()) {
            for (EquipmentSlot slot : ARMOR_SLOTS) set.baseMaterial(slot).ifPresent(bases::add);
        }
        for (Material base : bases) {
            NamespacedKey key = Keys.key("netherite_upgrade_" + base.name().toLowerCase(Locale.ROOT));
            Bukkit.addRecipe(new SmithingTransformRecipe(key, new ItemStack(Material.NETHERITE_HELMET),
                    new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                    RecipeChoice.predicateChoice(ArmorSets::isUpgradeable, new ItemStack(base)),
                    new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT),
                    false));
            RECIPES.add(key);
        }
    }

    /// A diamond-tier set piece on another base material, which only our upgrade recipe can upgrade.
    public static boolean isUpgradeable(@Nullable ItemStack item) {
        return CustomArmor.isConverted(item) && CustomArmor.tierOf(item) == ArmorTier.DIAMOND;
    }

    private static void register(Plugin plugin, ArmorSet set) {
        SETS.put(set.id(), set);
        set.registerRecipes(plugin);

        // Template + any armor piece + empty addition slot. The real result is built in
        // ArmorSmithingListener; this recipe only makes the smithing table accept the inputs.
        NamespacedKey key = Keys.key(set.id() + "_smithing");
        ItemStack template = set.createTemplate();
        Bukkit.addRecipe(new SmithingTransformRecipe(key, new ItemStack(Material.DIAMOND_BOOTS),
                RecipeChoice.predicateChoice(set::isTemplate, template),
                new RecipeChoice.MaterialChoice(ArmorTier.armorMaterials()),
                RecipeChoice.empty(),
                false));
        RECIPES.add(key);

        // Register listener-based effects once each.
        Set<SetEffect> effects = new HashSet<>();
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            set.pieceEffect(slot).ifPresent(effects::add);
        }
        set.fullSetBonus().ifPresent(effects::add);
        for (SetEffect effect : effects) {
            if (effect instanceof Listener listener) Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }

    /// Records a recipe so it is removed on disable and shown to players in the recipe book.
    public static void trackRecipe(NamespacedKey key) {
        RECIPES.add(key);
    }

    public static List<NamespacedKey> recipes() {
        return List.copyOf(RECIPES);
    }

    public static void unregisterAll() {
        RECIPES.forEach(Bukkit::removeRecipe);
        RECIPES.clear();
        SETS.clear();
    }

    public static @Nullable ArmorSet byId(String id) {
        return SETS.get(id);
    }

    /// The set a template makes. Races share one template between their items, so a set's template
    /// needn't carry the set's own id (the Alqualondë set is made with the Pearl of Ossë).
    public static @Nullable ArmorSet byTemplate(@Nullable ItemStack item) {
        if (!CustomItems.isCustom(item)) return null;
        for (ArmorSet set : SETS.values()) {
            if (set.isTemplate(item)) return set;
        }
        return null;
    }

    public static Iterable<ArmorSet> all() {
        return SETS.values();
    }

    /// Every armor item obtainable by command: each set's template, keyed by its item id (`arkenstone`,
    /// `pearl_of_osse`), and each of its pieces (`arkenstone_diamond_boots`).
    public static Map<String, Supplier<ItemStack>> giveableItems() {
        Map<String, Supplier<ItemStack>> items = new LinkedHashMap<>();
        for (ArmorSet set : SETS.values()) {
            items.putIfAbsent(CustomItems.idOf(set.createTemplate()), set::createTemplate);
            for (Material material : ArmorTier.armorMaterials()) {
                items.put(set.id() + "_" + material.name().toLowerCase(Locale.ROOT),
                        () -> CustomArmor.apply(ItemStack.of(material), set));
            }
        }
        return items;
    }
}
