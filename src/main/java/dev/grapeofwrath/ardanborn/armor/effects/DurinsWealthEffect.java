package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.Ores;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/// Full set: ores sometimes yield double.
public final class DurinsWealthEffect extends PieceEffect implements Listener {
    public DurinsWealthEffect(String setId) {
        super("durins_wealth", "Durin's Wealth", setId, null, OriginCheck.DWARF);
    }

    @Override
    protected List<String> lines() {
        return List.of("At times ores drop double", "Dwarves: more often");
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(BlockDropItemEvent event) {
        Material ore = event.getBlockState().getType();
        Player player = event.getPlayer();
        if (!Ores.isOre(ore) || !worn(player)) return;
        if (!ItemPower.roll(id, own(player) ? 0.15 : 0.10)) return;
        boolean doubled = false;
        for (Item drop : event.getItems()) {
            ItemStack stack = drop.getItemStack();
            if (stack.getType() == ore) continue; // Silk Touch: the ore block itself
            stack.setAmount(Math.min(stack.getMaxStackSize(), stack.getAmount() * 2));
            drop.setItemStack(stack);
            doubled = true;
        }
        if (doubled) player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.7f, 1.4f);
    }
}
