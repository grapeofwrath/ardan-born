package dev.grapeofwrath.ardanborn.weapons.runes;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.Ores;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

/// What each [Rune] does. Runes work for whoever holds the item; only inscribing them is limited to Smiths.
public final class RuneEffects implements Listener {
    private static final double WESTERNESSE_BONUS = 1.3;
    private static final int FROST_TICKS = 40;
    private static final double WARNING_RANGE = 16;
    /// On arrows shot from a runed bow or crossbow: the bow's runes, so they apply when the arrow lands.
    private static final NamespacedKey PROJECTILE_RUNES = Keys.key("projectile_runes");
    private static final Particle.DustOptions WARNING_GLOW = new Particle.DustOptions(Color.fromRGB(0x6FB8FF), 0.8f);

    /// Delving: what each ore's raw drop smelts into.
    private static final Map<Material, Material> SMELTED = Map.of(
            Material.RAW_IRON, Material.IRON_INGOT,
            Material.RAW_COPPER, Material.COPPER_INGOT,
            Material.RAW_GOLD, Material.GOLD_INGOT);
    private static final List<Material> DELVING_ORES = List.of(
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE);

    /// Harvest: each crop and the item it's replanted from.
    private static final Map<Material, Material> SEEDS = Map.of(
            Material.WHEAT, Material.WHEAT_SEEDS,
            Material.CARROTS, Material.CARROT,
            Material.POTATOES, Material.POTATO,
            Material.BEETROOTS, Material.BEETROOT_SEEDS,
            Material.NETHER_WART, Material.NETHER_WART);
    private static final int VEIN_LIMIT = 16;

    private Plugin plugin;
    /// Set while Vein breaks blocks, so those breaks don't start veins of their own.
    private static boolean veining = false;

    void start(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, this::warn, 20L, 20L);
    }

    // ------------------------------------------------------------------ Westernesse and Frost (melee)

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity victim)) return;
        List<Rune> runes;
        boolean melee = e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;
        if (e.getDamager() instanceof Player player && melee) {
            runes = Runes.on(player.getInventory().getItemInMainHand());
        } else if (e.getDamager() instanceof Trident trident) {
            runes = Runes.on(trident.getItemStack());
        } else if (e.getDamager() instanceof Projectile projectile) {
            runes = projectileRunes(projectile);
        } else {
            return;
        }
        if (runes.isEmpty()) return;

        if (runes.contains(Rune.WESTERNESSE) && melee && OriginAbilities.UNDEAD.contains(victim.getType())) {
            e.setDamage(e.getDamage() * WESTERNESSE_BONUS);
        }
        if (runes.contains(Rune.FROST)) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, FROST_TICKS, 0, true, true, true));
            victim.getWorld().spawnParticle(Particle.SNOWFLAKE, victim.getLocation().add(0, 1, 0), 8, 0.3, 0.4, 0.3, 0.01);
        }
    }

    // ------------------------------------------------------------------ Frost (bows and crossbows)

    @EventHandler(ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent e) {
        List<Rune> runes = Runes.on(e.getBow());
        if (runes.isEmpty() || !(e.getProjectile() instanceof Projectile projectile)) return;
        List<String> ids = runes.stream().map(Rune::id).toList();
        projectile.getPersistentDataContainer().set(PROJECTILE_RUNES, PersistentDataType.LIST.strings(), ids);
    }

    private static List<Rune> projectileRunes(Projectile projectile) {
        List<String> ids = projectile.getPersistentDataContainer().get(PROJECTILE_RUNES, PersistentDataType.LIST.strings());
        if (ids == null) return List.of();
        return ids.stream().map(Rune::byId).filter(r -> r != null).toList();
    }

    // ------------------------------------------------------------------ Warning (Sting)

    private void warn() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Runes.has(player.getInventory().getItemInMainHand(), Rune.WARNING)) continue;
            boolean near = false;
            for (Entity entity : player.getNearbyEntities(WARNING_RANGE, WARNING_RANGE / 2, WARNING_RANGE)) {
                if (entity instanceof Monster) {
                    near = true;
                    break;
                }
            }
            if (!near) continue;
            player.sendActionBar(Component.text("Your blade is kindled blue", Rune.WARNING.color()));
            Location hand = player.getLocation().add(player.getLocation().getDirection().multiply(0.5)).add(0, 1.0, 0);
            player.getWorld().spawnParticle(Particle.DUST, hand, 6, 0.15, 0.25, 0.15, 0, WARNING_GLOW);
        }
    }

    // ------------------------------------------------------------------ Delving

    /// Runs for every block a player breaks, including blocks broken through `Player#breakBlock`, so Fortune's
    /// extra drops are smelted too.
    @EventHandler(ignoreCancelled = true)
    public void onDrops(BlockDropItemEvent e) {
        if (!DELVING_ORES.contains(e.getBlockState().getType())) return;
        ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
        if (!Runes.has(tool, Rune.DELVING)) return;
        for (Item drop : e.getItems()) {
            Material smelted = SMELTED.get(drop.getItemStack().getType());
            if (smelted != null) drop.setItemStack(drop.getItemStack().withType(smelted));
        }
    }

    // ------------------------------------------------------------------ Harvest

    /// Right-click a ripe crop with a Harvest hoe: every ripe crop in the 3×3 around it (same height) is
    /// reaped and replanted from its own drops. Each crop's break is checked with a BlockBreakEvent first,
    /// so protection plugins still apply.
    @EventHandler(ignoreCancelled = true)
    public void onReap(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND) return;
        Block clicked = e.getClickedBlock();
        if (clicked == null || !isRipe(clicked)) return;
        Player player = e.getPlayer();
        ItemStack hoe = player.getInventory().getItemInMainHand();
        if (!Runes.has(hoe, Rune.HARVEST)) return;
        e.setCancelled(true);

        boolean reaped = false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block crop = clicked.getRelative(dx, 0, dz);
                if (!isRipe(crop)) continue;
                BlockBreakEvent check = new BlockBreakEvent(crop, player);
                check.setDropItems(false);
                if (!check.callEvent()) continue;
                List<ItemStack> drops = new ArrayList<>(crop.getDrops(hoe, player));
                takeSeed(drops, SEEDS.get(crop.getType()));
                Ageable young = (Ageable) crop.getBlockData();
                young.setAge(0);
                crop.setBlockData(young);
                Location at = crop.getLocation().add(0.5, 0.5, 0.5);
                for (ItemStack drop : drops) {
                    if (!drop.isEmpty()) crop.getWorld().dropItemNaturally(at, drop);
                }
                reaped = true;
            }
        }
        if (reaped) {
            player.damageItemStack(EquipmentSlot.HAND, 1);
            player.playSound(player.getLocation(), Sound.ITEM_CROP_PLANT, 1f, 1f);
        }
    }

    private static boolean isRipe(Block block) {
        return SEEDS.containsKey(block.getType())
                && block.getBlockData() instanceof Ageable crop && crop.getAge() == crop.getMaximumAge();
    }

    /// Removes one seed from the drops, for replanting. A crop with no seed among its drops is still
    /// replanted; the hoe's magic makes up the difference.
    private static void takeSeed(List<ItemStack> drops, Material seed) {
        for (ItemStack drop : drops) {
            if (drop.getType() == seed && drop.getAmount() > 0) {
                drop.setAmount(drop.getAmount() - 1);
                return;
            }
        }
    }

    // ------------------------------------------------------------------ Vein

    /// Sneak-mining an ore with a Vein pickaxe breaks the connected ore of the same kind, up to 16 more
    /// blocks. Each block is broken as if by the player (`Player#breakBlock`), so protection plugins,
    /// Fortune, Delving, the tool's durability and every other drop effect apply as normal.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMine(BlockBreakEvent e) {
        if (veining) return;
        Player player = e.getPlayer();
        if (!player.isSneaking()) return;
        Block origin = e.getBlock();
        Material ore = origin.getType();
        if (!Ores.isOre(ore)) return;
        if (!Runes.has(player.getInventory().getItemInMainHand(), Rune.VEIN)) return;

        List<Block> vein = connected(origin, ore);
        if (vein.isEmpty()) return;
        // Next tick, once the first block is gone.
        Bukkit.getScheduler().runTask(plugin, () -> {
            veining = true;
            try {
                for (Block block : vein) {
                    if (!player.isOnline() || block.getType() != ore) continue;
                    if (!Runes.has(player.getInventory().getItemInMainHand(), Rune.VEIN)) break; // tool broke
                    player.breakBlock(block);
                }
            } finally {
                veining = false;
            }
        });
    }

    /// Up to [#VEIN_LIMIT] blocks of the same ore touching the origin (including diagonally), nearest first.
    private static List<Block> connected(Block origin, Material ore) {
        List<Block> found = new ArrayList<>();
        Set<Block> seen = new HashSet<>();
        Deque<Block> queue = new ArrayDeque<>();
        seen.add(origin);
        queue.add(origin);
        while (!queue.isEmpty() && found.size() < VEIN_LIMIT) {
            Block current = queue.poll();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        Block next = current.getRelative(dx, dy, dz);
                        if (next.getType() != ore || !seen.add(next)) continue;
                        found.add(next);
                        queue.add(next);
                        if (found.size() >= VEIN_LIMIT) return found;
                    }
                }
            }
        }
        return found;
    }
}
