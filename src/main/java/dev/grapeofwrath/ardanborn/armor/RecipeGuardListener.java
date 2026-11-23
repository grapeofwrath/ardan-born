package dev.grapeofwrath.ardanborn.armor;

import dev.grapeofwrath.ardanborn.Keys;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Crafter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Objects;

/// Custom items are built on vanilla materials; stop them being used up or stripped in vanilla recipes.
///
/// Set pieces are guarded too: e.g. vanilla's repair recipe (two boots in a crafting grid) builds a fresh
/// item and drops our data. Dyeing is allowed, since it copies the item with a new color.
public final class RecipeGuardListener implements Listener {

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (isAllowed(event.getRecipe())) return;
        if (containsGuarded(event.getInventory().getMatrix())) event.getInventory().setResult(null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCrafter(CrafterCraftEvent event) {
        if (isAllowed(event.getRecipe())) return;
        if (event.getBlock().getState(false) instanceof Crafter crafter
                && containsGuarded(crafter.getInventory().getContents())) {
            event.setCancelled(true);
        }
    }

    /// Combining a set piece with another item of the same material only works with a piece of the same
    /// set and tier. Otherwise leather-based boots could be repaired with plain leather boots.
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack left = event.getView().getTopInventory().getFirstItem();
        ItemStack right = event.getView().getTopInventory().getSecondItem();
        if (!CustomArmor.isSetPiece(left) || right == null || right.getType() != left.getType()) return;
        if (CustomArmor.setOf(right) != CustomArmor.setOf(left)
                || !Objects.equals(CustomArmor.tierOf(right), CustomArmor.tierOf(left))) {
            event.setResult(null);
        }
    }

    /// Artifacts are ordinary fuels and brewing ingredients underneath (a stick, a breeze rod).
    @EventHandler(ignoreCancelled = true)
    public void onBurn(FurnaceBurnEvent event) {
        if (CustomItems.isCustom(event.getFuel())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBrew(BrewEvent event) {
        if (CustomItems.isCustom(event.getContents().getIngredient())) event.setCancelled(true);
    }

    private static boolean isAllowed(Recipe recipe) {
        if (!(recipe instanceof Keyed keyed)) return false;
        NamespacedKey key = keyed.getKey();
        return key.getNamespace().equals(Keys.NAMESPACE)
                // Vanilla dye recipes, e.g. minecraft:leather_boots_dyed.
                || (key.getNamespace().equals(NamespacedKey.MINECRAFT) && key.getKey().endsWith("_dyed"));
    }

    private static boolean containsGuarded(ItemStack[] items) {
        for (ItemStack item : items) {
            if (CustomItems.isCustom(item) || CustomArmor.isSetPiece(item)) return true;
        }
        return false;
    }
}
