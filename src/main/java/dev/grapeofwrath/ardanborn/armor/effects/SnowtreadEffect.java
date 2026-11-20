package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/// Boots: walk on powder snow without sinking, like leather boots, and never freeze.
///
/// Vanilla hard-codes powder-snow walking to `minecraft:leather_boots` (checked on both server and
/// client), so it can't be granted per item. Instead, the powder snow just below the wearer's feet
/// is shown to them alone as solid snow. Their client then stands on it, and since the server only
/// treats a player as "in" powder snow when they overlap it, they are never slowed or frozen.
/// Sneaking lets the wearer sink in on purpose, as with leather boots.
public final class SnowtreadEffect implements SetEffect, Listener {
    private static final int RADIUS = 2;
    private static final BlockData SOLID = Material.SNOW_BLOCK.createBlockData();

    /// Per wearer: blocks currently shown as solid snow.
    private final Map<UUID, Set<Location>> faked = new HashMap<>();

    @Override
    public String name() {
        return "Snowtread";
    }

    @Override
    public List<Component> describe(ArmorTier tier) {
        return List.of(
                Component.text("Walk on powder snow without sinking"),
                Component.text("Immune to freezing"));
    }

    @Override
    public void onEquip(Player player, ArmorTier tier) {
        faked.putIfAbsent(player.getUniqueId(), new HashSet<>());
    }

    @Override
    public void onUnequip(Player player) {
        Set<Location> shown = faked.remove(player.getUniqueId());
        if (shown != null) revert(player, shown);
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        Set<Location> shown = faked.computeIfAbsent(player.getUniqueId(), _ -> new HashSet<>());
        if (player.getFreezeTicks() > 0) player.setFreezeTicks(0);

        Set<Location> wanted = new HashSet<>();
        if (!player.isSneaking() && player.getGameMode() != GameMode.SPECTATOR) {
            Location feet = player.getLocation();
            World world = feet.getWorld();
            // Only blocks whose top is at or below the feet: never the block the player is inside.
            int topY = (int) Math.floor(feet.getY() + 0.001) - 1;
            for (int y = topY - 1; y <= topY; y++) {
                for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                    for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                        Block block = world.getBlockAt(feet.getBlockX() + dx, y, feet.getBlockZ() + dz);
                        if (block.getType() == Material.POWDER_SNOW) wanted.add(block.getLocation());
                    }
                }
            }
        }

        Set<Location> stale = new HashSet<>(shown);
        stale.removeAll(wanted);
        revert(player, stale);
        // Resend every time: the server overwrites the fake whenever the real block is resent.
        for (Location location : wanted) player.sendBlockChange(location, SOLID);
        shown.clear();
        shown.addAll(wanted);
    }

    private static void revert(Player player, Set<Location> locations) {
        for (Location location : locations) {
            if (location.getWorld() != player.getWorld() || !location.isChunkLoaded()) continue;
            player.sendBlockChange(location, location.getBlock().getBlockData());
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFreeze(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FREEZE
                && event.getEntity() instanceof Player player
                && faked.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
