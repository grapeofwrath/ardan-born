package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.ArdanBorn;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.CustomArmor;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Helmet: sneak and right-click with a pickaxe to sense the ores around you. They glow through the
/// stone for you alone, for 30 seconds or until you step away. Mined ore stops glowing. Naugrim sense further.
public final class OreSenseEffect implements SetEffect, Listener {
    private static final int DIAMOND_RADIUS = 8;
    private static final int NETHERITE_RADIUS = 12;
    private static final int NAUGRIM_BONUS = 4;
    private static final int MAX_MARKS = 256;
    private static final long SHOW_TICKS = 600;
    private static final long COOLDOWN_MS = 60_000;

    private static final Map<Material, Color> COLORS = Map.ofEntries(
            Map.entry(Material.COAL_ORE, Color.fromRGB(0x3A3A3A)),
            Map.entry(Material.DEEPSLATE_COAL_ORE, Color.fromRGB(0x3A3A3A)),
            Map.entry(Material.IRON_ORE, Color.fromRGB(0xD8AF93)),
            Map.entry(Material.DEEPSLATE_IRON_ORE, Color.fromRGB(0xD8AF93)),
            Map.entry(Material.COPPER_ORE, Color.fromRGB(0xE07A4E)),
            Map.entry(Material.DEEPSLATE_COPPER_ORE, Color.fromRGB(0xE07A4E)),
            Map.entry(Material.GOLD_ORE, Color.fromRGB(0xFFD83D)),
            Map.entry(Material.DEEPSLATE_GOLD_ORE, Color.fromRGB(0xFFD83D)),
            Map.entry(Material.NETHER_GOLD_ORE, Color.fromRGB(0xFFD83D)),
            Map.entry(Material.REDSTONE_ORE, Color.fromRGB(0xFF2020)),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE, Color.fromRGB(0xFF2020)),
            Map.entry(Material.LAPIS_ORE, Color.fromRGB(0x2050FF)),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE, Color.fromRGB(0x2050FF)),
            Map.entry(Material.DIAMOND_ORE, Color.fromRGB(0x5FF5E8)),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE, Color.fromRGB(0x5FF5E8)),
            Map.entry(Material.EMERALD_ORE, Color.fromRGB(0x20D040)),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE, Color.fromRGB(0x20D040)),
            Map.entry(Material.NETHER_QUARTZ_ORE, Color.fromRGB(0xFFFFFF)),
            Map.entry(Material.ANCIENT_DEBRIS, Color.fromRGB(0x8B5A3C)));

    private final String setId;
    private final OriginAbilities.Cooldown cooldown = new OriginAbilities.Cooldown();
    private final Map<UUID, Reveal> active = new HashMap<>();

    private record Reveal(Map<Block, BlockDisplay> marks, BukkitTask expiry) {}

    public OreSenseEffect(String setId) {
        this.setId = setId;
    }

    @Override
    public String name() {
        return "Ore-sense";
    }

    @Override
    public List<Component> describe(ArmorTier tier) {
        return List.of(
                Component.text("Sneak and right-click with a pickaxe:"),
                Component.text("ores within " + radius(tier) + " blocks glow for you"),
                Component.text("for 30 seconds, or until you move."),
                Component.text("Once a minute."),
                Component.text("Naugrim: +" + NAUGRIM_BONUS + " blocks"));
    }

    private static int radius(ArmorTier tier) {
        return tier == ArmorTier.NETHERITE ? NETHERITE_RADIUS : DIAMOND_RADIUS;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        if (!Tag.ITEMS_PICKAXES.isTagged(player.getInventory().getItemInMainHand().getType())) return;
        ArmorTier tier = CustomArmor.wornTier(player, EquipmentSlot.HEAD, setId);
        if (tier == null) return;

        // Don't open a chest or press a button as well.
        event.setCancelled(true);
        if (!cooldown.tryUse(player, COOLDOWN_MS)) return;

        int radius = radius(tier) + (OriginCheck.is(player, OriginCheck.NAUGRIM) ? NAUGRIM_BONUS : 0);
        int found = reveal(player, radius);
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1f, 0.7f);
        player.sendActionBar(Component.text(found == 0 ? "You sense no ore nearby" : "You sense " + found + " ore",
                NamedTextColor.GOLD));
    }

    /// Marks every ore within `radius` with a glowing copy only `player` can see. Returns how many.
    private int reveal(Player player, int radius) {
        Plugin plugin = JavaPlugin.getPlugin(ArdanBorn.class);
        UUID id = player.getUniqueId();
        clear(id);
        World world = player.getWorld();
        Block center = player.getLocation().getBlock();
        int minY = Math.max(world.getMinHeight(), center.getY() - radius);
        int maxY = Math.min(world.getMaxHeight() - 1, center.getY() + radius);
        Map<Block, BlockDisplay> marks = new HashMap<>();
        scan:
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = minY; y <= maxY; y++) {
                    Block block = world.getBlockAt(center.getX() + x, y, center.getZ() + z);
                    Color color = COLORS.get(block.getType());
                    if (color == null) continue;
                    marks.put(block, world.spawn(block.getLocation(), BlockDisplay.class, display -> {
                        display.setBlock(block.getBlockData());
                        display.setGlowing(true);
                        display.setGlowColorOverride(color);
                        display.setVisibleByDefault(false);
                        display.setPersistent(false);
                    }));
                    if (marks.size() >= MAX_MARKS) break scan;
                }
            }
        }
        if (marks.isEmpty()) return 0;
        for (BlockDisplay mark : marks.values()) player.showEntity(plugin, mark);
        BukkitTask expiry = plugin.getServer().getScheduler().runTaskLater(plugin, () -> clear(id), SHOW_TICKS);
        active.put(id, new Reveal(marks, expiry));
        return marks.size();
    }

    private void clear(UUID id) {
        Reveal reveal = active.remove(id);
        if (reveal == null) return;
        reveal.expiry().cancel();
        reveal.marks().values().forEach(BlockDisplay::remove);
    }

    /// Drops the marks on blocks that no longer hold ore.
    private void unmark(Collection<Block> blocks) {
        if (active.isEmpty()) return;
        for (Reveal reveal : active.values()) {
            for (Block block : blocks) {
                BlockDisplay mark = reveal.marks().remove(block);
                if (mark != null) mark.remove();
            }
        }
    }

    // Looking around is fine; stepping away ends the sense.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (active.isEmpty() || !event.hasChangedPosition()) return;
        clear(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        clear(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clear(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        unmark(List.of(event.getBlock()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        unmark(event.blockList());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        unmark(event.blockList());
    }
}
