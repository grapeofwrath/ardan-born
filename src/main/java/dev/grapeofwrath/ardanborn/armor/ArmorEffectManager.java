package dev.grapeofwrath.ardanborn.armor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/// Tracks which set effects each player has active, and at what tier.
public final class ArmorEffectManager implements Listener {
    /// How often (in ticks) [SetEffect#tick] runs.
    public static final long TICK_PERIOD = 2L;

    private static final List<EquipmentSlot> ARMOR_SLOTS =
            List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    private final Plugin plugin;
    private final Map<UUID, Map<SetEffect, ArmorTier>> active = new HashMap<>();
    private BukkitTask task;

    public ArmorEffectManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Bukkit.getOnlinePlayers().forEach(this::recompute);
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tickAll, TICK_PERIOD, TICK_PERIOD);
    }

    public void shutdown() {
        if (task != null) task.cancel();
        for (UUID id : List.copyOf(active.keySet())) {
            Player player = Bukkit.getPlayer(id);
            Map<SetEffect, ArmorTier> effects = active.remove(id);
            if (player != null && effects != null) effects.keySet().forEach(e -> e.onUnequip(player));
        }
    }

    private void tickAll() {
        for (var entry : List.copyOf(active.entrySet())) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;
            entry.getValue().forEach((effect, tier) -> effect.tick(player, tier));
        }
    }

    /// Re-reads the player's armor and applies equip/unequip changes.
    public void recompute(Player player) {
        Map<SetEffect, ArmorTier> next = new HashMap<>();
        Map<ArmorSet, ArmorTier> lowestTier = new HashMap<>();
        Map<ArmorSet, Integer> pieces = new HashMap<>();

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack item = player.getInventory().getItem(slot);
            ArmorSet set = CustomArmor.setOf(item);
            ArmorTier tier = CustomArmor.tierOf(item);
            if (set == null || tier == null || ArmorTier.slotOf(item) != slot) continue;

            set.pieceEffect(slot).ifPresent(effect -> next.merge(effect, tier, ArmorEffectManager::higher));
            pieces.merge(set, 1, Integer::sum);
            lowestTier.merge(set, tier, (a, b) -> a.compareTo(b) <= 0 ? a : b);
        }
        pieces.forEach((set, count) -> {
            if (count == ARMOR_SLOTS.size()) {
                set.fullSetBonus().ifPresent(bonus -> next.merge(bonus, lowestTier.get(set), ArmorEffectManager::higher));
            }
        });

        Map<SetEffect, ArmorTier> previous = active.getOrDefault(player.getUniqueId(), Map.of());
        previous.forEach((effect, tier) -> {
            if (!Objects.equals(next.get(effect), tier)) effect.onUnequip(player);
        });
        next.forEach((effect, tier) -> {
            if (!Objects.equals(previous.get(effect), tier)) effect.onEquip(player, tier);
        });

        if (next.isEmpty()) active.remove(player.getUniqueId());
        else active.put(player.getUniqueId(), next);
    }

    private static ArmorTier higher(ArmorTier a, ArmorTier b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArmorChange(PlayerArmorChangeEvent event) {
        recompute(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        recompute(player);
        // Show our recipes (Arkenstone etc.) in the recipe book.
        player.discoverRecipes(ArmorSets.recipes());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) recompute(player);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Map<SetEffect, ArmorTier> effects = active.remove(player.getUniqueId());
        if (effects != null) effects.keySet().forEach(e -> e.onUnequip(player));
    }
}
