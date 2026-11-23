package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.armor.sets.ArkenstoneSet;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/// The Arkenstone pickaxe: the Arkenstone (the dwarves' template, which also makes the armor set)
/// combined with a diamond or netherite pickaxe. It mines 3×3, in the plane of the face you dig into;
/// sneak to mine a single block. Naugrim wear it down only for the block they aim at.
public final class ArkenstonePickaxe implements ToolUpgrade, Listener {
    public static final String ID = "arkenstone_pickaxe";
    private static final TextColor COLOR = TextColor.color(0xD8ECFF);
    /// Neighbors harder than this aren't broken unless the center block is at least as hard. This takes
    /// every ore (deepslate ores are 4.5) but not obsidian or ancient debris.
    private static final float HARDNESS_FLOOR = 5.0f;

    /// The face each player last started digging, which decides the 3×3 plane.
    private final Map<UUID, BlockFace> digFace = new HashMap<>();
    /// Players whose extra blocks are being broken right now, so those breaks don't spread again.
    private final Set<UUID> boring = new HashSet<>();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public ItemStack template() {
        return ArkenstoneSet.template();
    }

    @Override
    public boolean isTemplate(@Nullable ItemStack item) {
        return ArkenstoneSet.ID.equals(CustomItems.idOf(item));
    }

    @Override
    public List<Material> baseMaterials() {
        return List.of(Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE);
    }

    @Override
    public ItemStack upgrade(ItemStack pickaxe) {
        ItemStack pick = pickaxe.clone();
        // No tier in the name, so a later netherite upgrade (which copies our data) needs no renaming.
        pick.setData(DataComponentTypes.ITEM_NAME, Component.text("Arkenstone Pickaxe", COLOR));
        pick.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        pick.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(ID).build());
        pick.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(ToolUpgrade.lore("Tunnel-bore", NamedTextColor.GOLD))
                .addLine(ToolUpgrade.lore("Mines 3×3; sneak to mine one block", NamedTextColor.GRAY))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Naugrim: the extra blocks", NamedTextColor.DARK_AQUA))
                .addLine(ToolUpgrade.lore("cost no durability", NamedTextColor.DARK_AQUA))
                .build());
        CustomItems.tag(pick, ID);
        return pick;
    }

    @EventHandler
    public void onDig(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            digFace.put(event.getPlayer().getUniqueId(), event.getBlockFace());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if (boring.contains(id) || player.isSneaking()) return;
        if (!is(player.getInventory().getItemInMainHand())) return;
        BlockFace face = digFace.get(id);
        if (face == null) return;

        Block center = event.getBlock();
        float limit = Math.max(center.getType().getHardness(), HARDNESS_FLOOR);
        boring.add(id);
        try {
            for (Block block : plane(center, face)) {
                // The pick may break partway through.
                if (!is(player.getInventory().getItemInMainHand())) break;
                if (canBore(block, limit)) player.breakBlock(block);
            }
        } finally {
            boring.remove(id);
        }
    }

    /// Naugrim: the extra blocks cost no durability.
    @EventHandler(ignoreCancelled = true)
    public void onWear(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        if (boring.contains(player.getUniqueId()) && OriginCheck.is(player, OriginCheck.NAUGRIM)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        digFace.remove(event.getPlayer().getUniqueId());
    }

    private static boolean canBore(Block block, float limit) {
        Material type = block.getType();
        if (type.isAir() || !Tag.MINEABLE_PICKAXE.isTagged(type)) return false;
        float hardness = type.getHardness();
        if (hardness < 0 || hardness > limit) return false;
        // Never spill a furnace, hopper or other container.
        return !(block.getState(false) instanceof TileState);
    }

    /// The 8 blocks around `center` in the plane facing `face`: flat when digging into a top or bottom,
    /// upright when digging into a side.
    private static List<Block> plane(Block center, BlockFace face) {
        int[][] offsets = switch (face) {
            case UP, DOWN -> new int[][]{{1, 0, 0}, {0, 0, 1}};
            case NORTH, SOUTH -> new int[][]{{1, 0, 0}, {0, 1, 0}};
            default -> new int[][]{{0, 0, 1}, {0, 1, 0}};
        };
        int[] a = offsets[0];
        int[] b = offsets[1];
        List<Block> blocks = new ArrayList<>(8);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                blocks.add(center.getRelative(a[0] * i + b[0] * j, a[1] * i + b[1] * j, a[2] * i + b[2] * j));
            }
        }
        return blocks;
    }
}
