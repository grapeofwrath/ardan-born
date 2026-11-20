package dev.grapeofwrath.ardanborn.armor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

/// Builds smithing results for set templates, and refreshes set pieces after vanilla upgrades.
public final class ArmorSmithingListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepare(PrepareSmithingEvent event) {
        SmithingInventory inventory = event.getInventory();
        ItemStack template = inventory.getInputTemplate();
        ItemStack base = inventory.getInputEquipment();

        ArmorSet set = ArmorSets.byTemplate(template);
        if (set != null) {
            ItemStack addition = inventory.getInputMineral();
            boolean valid = base != null && ArmorTier.of(base.getType()) != null
                    && !CustomArmor.isSetPiece(base)
                    && (addition == null || addition.isEmpty());
            event.setResult(valid ? CustomArmor.apply(base.asOne(), set) : null);
            return;
        }

        // Vanilla upgrades (netherite) copy our data onto the result; update its name and lore.
        ItemStack result = event.getResult();
        if (CustomArmor.isSetPiece(result)) {
            event.setResult(CustomArmor.refresh(result.clone()));
        }
    }
}
