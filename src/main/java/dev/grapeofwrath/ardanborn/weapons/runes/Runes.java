package dev.grapeofwrath.ardanborn.weapons.runes;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorSets;
import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.weapons.ToolUpgrade;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/// Cirth runes: rune-stones anyone can craft, which only a Smith can inscribe onto a weapon or tool at a
/// smithing table (rune-stone + item + lapis). Netherite items hold two runes, everything else one; when
/// an item is full, a new rune replaces the oldest.
///
/// Runes are kept in the item's persistent data as a list of rune ids, oldest first, and listed at the end
/// of its lore.
public final class Runes implements Listener {
    /// On inscribed items: the rune ids, oldest first.
    private static final NamespacedKey RUNES = Keys.key("runes");
    /// On rune-stones: which rune the stone carries.
    private static final NamespacedKey STONE = Keys.key("rune_stone");
    private static final String STONE_ID_PREFIX = "rune_stone_";
    private static final NamespacedKey INSCRIBE_RECIPE = Keys.key("rune_inscription");
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    public static void registerAll(Plugin plugin) {
        for (Rune rune : Rune.values()) {
            NamespacedKey key = Keys.key(STONE_ID_PREFIX + rune.id());
            ShapedRecipe recipe = new ShapedRecipe(key, stone(rune));
            recipe.shape("ABA", "BXB", "ABA");
            recipe.setIngredient('A', rune.ingredientA());
            recipe.setIngredient('B', rune.ingredientB());
            recipe.setIngredient('X', Material.AMETHYST_SHARD);
            Bukkit.addRecipe(recipe);
            ArmorSets.trackRecipe(key);
        }

        // Lets the smithing table accept a rune-stone, an item a rune fits and lapis. The real result is
        // built in onPrepare, and only for Smiths.
        List<Material> runeable = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isLegacy() || !material.isItem()) continue;
            for (Rune rune : Rune.values()) {
                if (rune.fits(material)) {
                    runeable.add(material);
                    break;
                }
            }
        }
        Bukkit.addRecipe(new SmithingTransformRecipe(INSCRIBE_RECIPE, new ItemStack(runeable.getFirst()),
                RecipeChoice.predicateChoice(Runes::isStone, stone(Rune.WESTERNESSE)),
                new RecipeChoice.MaterialChoice(runeable),
                new RecipeChoice.MaterialChoice(Material.LAPIS_LAZULI),
                false));
        ArmorSets.trackRecipe(INSCRIBE_RECIPE);

        Bukkit.getPluginManager().registerEvents(new Runes(), plugin);
        new RuneEffects().start(plugin);
    }

    // ------------------------------------------------------------------ rune-stones

    public static ItemStack stone(Rune rune) {
        // Looks like an amethyst shard, but is built on flint so it is a different item from the shards in
        // its own recipe. RecipeGuardListener keeps it out of vanilla recipes.
        ItemStack item = ItemStack.of(Material.FLINT);
        item.setData(DataComponentTypes.ITEM_MODEL, Material.AMETHYST_SHARD.getKey());
        item.setData(DataComponentTypes.ITEM_NAME, Component.text(rune.displayName() + " Rune-stone", rune.color()));
        item.setData(DataComponentTypes.RARITY, ItemRarity.RARE);
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA,
                CustomModelData.customModelData().addString(STONE_ID_PREFIX + rune.id()).build());
        item.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(ToolUpgrade.lore(rune.effect(), NamedTextColor.GRAY))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("A Smith can inscribe it at a", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("Smithing Table, with lapis.", NamedTextColor.BLUE))
                .build());
        item.editPersistentDataContainer(pdc -> pdc.set(STONE, PersistentDataType.STRING, rune.id()));
        CustomItems.tag(item, STONE_ID_PREFIX + rune.id());
        return item;
    }

    public static boolean isStone(@Nullable ItemStack item) {
        return stoneRune(item) != null;
    }

    public static @Nullable Rune stoneRune(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        return Rune.byId(item.getPersistentDataContainer().get(STONE, PersistentDataType.STRING));
    }

    /** `/ardanborn give` ids: `rune_stone_<rune>`. */
    public static Map<String, Supplier<ItemStack>> giveableItems() {
        Map<String, Supplier<ItemStack>> items = new LinkedHashMap<>();
        for (Rune rune : Rune.values()) items.put(STONE_ID_PREFIX + rune.id(), () -> stone(rune));
        return items;
    }

    // ------------------------------------------------------------------ inscribed items

    /** The runes on an item, oldest first. */
    public static List<Rune> on(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return List.of();
        List<String> ids = item.getPersistentDataContainer().get(RUNES, PersistentDataType.LIST.strings());
        if (ids == null) return List.of();
        List<Rune> runes = new ArrayList<>();
        for (String id : ids) {
            Rune rune = Rune.byId(id);
            if (rune != null) runes.add(rune);
        }
        return runes;
    }

    public static boolean has(@Nullable ItemStack item, Rune rune) {
        return on(item).contains(rune);
    }

    /** Netherite items hold two runes; everything else holds one. */
    public static int slots(Material material) {
        return material.name().startsWith("NETHERITE_") ? 2 : 1;
    }

    /// A copy of `item` with `rune` added (replacing the oldest rune if the item is full), or null if the
    /// rune doesn't fit the item or is already on it.
    public static @Nullable ItemStack inscribe(ItemStack item, Rune rune) {
        if (!rune.fits(item.getType())) return null;
        List<Rune> runes = new ArrayList<>(on(item));
        if (runes.contains(rune)) return null;
        while (runes.size() >= slots(item.getType())) runes.removeFirst();
        runes.add(rune);
        ItemStack result = item.clone();
        List<String> ids = runes.stream().map(Rune::id).toList();
        result.editPersistentDataContainer(pdc -> pdc.set(RUNES, PersistentDataType.LIST.strings(), ids));
        refreshLore(result);
        return result;
    }

    /// Rewrites the rune lines at the end of an item's lore to match its runes, keeping every other line.
    /// Call after anything that rebuilds an item's lore (e.g. a race-item upgrade of a runed weapon).
    public static void refreshLore(ItemStack item) {
        Set<String> runeLines = new HashSet<>();
        for (Rune rune : Rune.values()) runeLines.add(PLAIN.serialize(nameLine(rune)));

        ItemLore current = item.getData(DataComponentTypes.LORE);
        List<Component> lines = new ArrayList<>(current == null ? List.of() : current.lines());
        lines.removeIf(line -> runeLines.contains(PLAIN.serialize(line)));
        while (!lines.isEmpty() && PLAIN.serialize(lines.getLast()).isEmpty()) lines.removeLast();

        List<Rune> runes = on(item);
        if (!runes.isEmpty() && !lines.isEmpty()) lines.add(Component.empty());
        for (Rune rune : runes) lines.add(nameLine(rune));

        if (lines.isEmpty()) {
            item.unsetData(DataComponentTypes.LORE);
        } else {
            item.setData(DataComponentTypes.LORE, ItemLore.lore(lines));
        }
    }

    private static Component nameLine(Rune rune) {
        return Component.text("◈ Rune of " + rune.displayName() + ": " + rune.effect(), rune.color())
                .decoration(TextDecoration.ITALIC, false);
    }

    // ------------------------------------------------------------------ smithing

    /// Runs after ToolUpgrades and the armor smithing listener, which ignore rune-stones.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepare(PrepareSmithingEvent event) {
        SmithingInventory inventory = event.getInventory();
        Rune rune = stoneRune(inventory.getInputTemplate());
        if (rune == null) return;
        ItemStack base = inventory.getInputEquipment();
        ItemStack addition = inventory.getInputMineral();
        boolean valid = base != null && !base.isEmpty()
                && addition != null && addition.getType() == Material.LAPIS_LAZULI
                && event.getView().getPlayer() instanceof Player player
                && OriginAbilities.SmithsMark.isSmith(player);
        event.setResult(valid ? inscribe(base.asOne(), rune) : null);
    }
}
