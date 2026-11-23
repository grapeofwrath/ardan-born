package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorEffectManager;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/// Boots: sprint across water for a few seconds. The water's surface is shown to the wearer's own client as a waterlogged barrier, so the world never changes.
public final class WaveWalkerEffect extends PieceEffect implements Listener {
    private static final int REACH = 3;
    private static final int TICKS = 100;
    private static final int OWN_TICKS = 200;
    private static final BlockData SURFACE = Material.BARRIER.createBlockData(
            data -> ((Waterlogged) data).setWaterlogged(true));

    private static final class Walk {
        final Set<Block> shown = new HashSet<>();
        int left;
    }

    private final Map<UUID, Walk> walks = new HashMap<>();

    public WaveWalkerEffect(String setId) {
        super("wave_walker", "Wave-walker", setId, EquipmentSlot.FEET, OriginCheck.TELERI);
    }

    @Override
    protected List<String> lines() {
        return List.of("For a while you may sprint across water", "Teleri: for longer");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        Walk walk = walks.computeIfAbsent(player.getUniqueId(), _ -> new Walk());
        Block below = player.getLocation().subtract(0, 0.2, 0).getBlock();
        if (below.isSolid()) walk.left = own(player) ? OWN_TICKS : TICKS;
        boolean walking = player.isSprinting() && !player.isInWater() && !player.isSneaking()
                && !player.isGliding() && !player.isFlying() && !player.isInsideVehicle() && walk.left > 0;
        if (!walking) {
            clear(player, walk);
            return;
        }
        if (below.getType() == Material.WATER) walk.left -= (int) ArmorEffectManager.TICK_PERIOD;

        // The water at or just under the feet, all around, so the next step always lands.
        Set<Block> wanted = new HashSet<>();
        Block feet = player.getLocation().getBlock();
        for (int x = -REACH; x <= REACH; x++) {
            for (int z = -REACH; z <= REACH; z++) {
                for (int y = -2; y <= -1; y++) {
                    Block block = feet.getRelative(x, y, z);
                    if (block.getType() == Material.WATER && block.getRelative(BlockFace.UP).isEmpty()) wanted.add(block);
                }
            }
        }
        for (Block block : List.copyOf(walk.shown)) {
            if (!wanted.contains(block)) restore(player, walk, block);
        }
        for (Block block : wanted) {
            if (walk.shown.add(block)) player.sendBlockChange(block.getLocation(), SURFACE);
        }
    }

    private static void restore(Player player, Walk walk, Block block) {
        walk.shown.remove(block);
        if (player.getWorld().equals(block.getWorld())) player.sendBlockChange(block.getLocation(), block.getBlockData());
    }

    private static void clear(Player player, Walk walk) {
        for (Block block : List.copyOf(walk.shown)) restore(player, walk, block);
    }

    @Override
    public void onUnequip(Player player) {
        Walk walk = walks.remove(player.getUniqueId());
        if (walk != null) clear(player, walk);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Walk walk = walks.get(event.getPlayer().getUniqueId());
        if (walk != null) walk.shown.clear();
    }
}
