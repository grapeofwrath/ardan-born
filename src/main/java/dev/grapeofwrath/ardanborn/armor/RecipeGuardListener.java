package dev.grapeofwrath.ardanborn.armor;

import dev.grapeofwrath.ardanborn.Keys;
import org.bukkit.Keyed;
import org.bukkit.block.Crafter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/// Custom items are built on vanilla materials; stop them being used up in vanilla recipes.
public final class RecipeGuardListener implements Listener {

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (isOurs(event.getRecipe())) return;
        if (containsCustom(event.getInventory().getMatrix())) event.getInventory().setResult(null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCrafter(CrafterCraftEvent event) {
        if (isOurs(event.getRecipe())) return;
        if (event.getBlock().getState(false) instanceof Crafter crafter
                && containsCustom(crafter.getInventory().getContents())) {
            event.setCancelled(true);
        }
    }

    private static boolean isOurs(Recipe recipe) {
        return recipe instanceof Keyed keyed && keyed.getKey().getNamespace().equals(Keys.NAMESPACE);
    }

    private static boolean containsCustom(ItemStack[] items) {
        for (ItemStack item : items) {
            if (CustomItems.isCustom(item)) return true;
        }
        return false;
    }
}
