package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorSets;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/// Registry of all [ToolUpgrade]s, and the smithing table logic that makes them.
public final class ToolUpgrades implements Listener {
    private static final List<ToolUpgrade> UPGRADES = List.of(
            new HavensTrident(), new Aeglos(), new ArkenstonePick());

    public static void registerAll(Plugin plugin) {
        for (ToolUpgrade upgrade : UPGRADES) {
            // Template + base item + empty addition slot. The real result is built in onPrepare; this recipe
            // only makes the smithing table accept the inputs.
            NamespacedKey key = Keys.key(upgrade.id() + "_smithing");
            Bukkit.addRecipe(new SmithingTransformRecipe(key, new ItemStack(upgrade.baseMaterials().getFirst()),
                    RecipeChoice.predicateChoice(upgrade::isTemplate, upgrade.template()),
                    new RecipeChoice.MaterialChoice(upgrade.baseMaterials()),
                    RecipeChoice.empty(),
                    false));
            ArmorSets.trackRecipe(key);
            if (upgrade instanceof Listener listener) Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
        Bukkit.getPluginManager().registerEvents(new ToolUpgrades(), plugin);
    }

    public static @Nullable ToolUpgrade of(@Nullable ItemStack item) {
        for (ToolUpgrade upgrade : UPGRADES) {
            if (upgrade.is(item)) return upgrade;
        }
        return null;
    }

    /// Every finished tool or weapon, keyed by id. Templates are listed by [ArmorSets#giveableItems].
    public static Map<String, Supplier<ItemStack>> giveableItems() {
        Map<String, Supplier<ItemStack>> items = new LinkedHashMap<>();
        for (ToolUpgrade upgrade : UPGRADES) {
            List<Material> bases = upgrade.baseMaterials();
            if (bases.size() == 1) {
                items.put(upgrade.id(), () -> upgrade.upgrade(ItemStack.of(bases.getFirst())));
                continue;
            }
            for (Material base : bases) {
                // By tier, e.g. aeglos_netherite (from NETHERITE_SPEAR).
                String tier = base.name().substring(0, base.name().indexOf('_')).toLowerCase(Locale.ROOT);
                items.put(upgrade.id() + "_" + tier,
                        () -> upgrade.upgrade(ItemStack.of(base)));
            }
        }
        return items;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepare(PrepareSmithingEvent event) {
        SmithingInventory inventory = event.getInventory();
        ItemStack template = inventory.getInputTemplate();
        ItemStack base = inventory.getInputEquipment();
        if (base == null || base.isEmpty()) return;
        for (ToolUpgrade upgrade : UPGRADES) {
            if (!upgrade.isTemplate(template) || !upgrade.baseMaterials().contains(base.getType())) continue;
            ItemStack addition = inventory.getInputMineral();
            boolean valid = of(base) == null && (addition == null || addition.isEmpty());
            event.setResult(valid ? upgrade.upgrade(base.asOne()) : null);
            return;
        }
    }
}
