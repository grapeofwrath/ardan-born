package dev.grapeofwrath.ardanborn.adornments;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/// What each player wears. It is kept in the player's persistent data, so that it is always saved together
/// with their inventory, with a copy in memory for quick checks.
public final class Adornments implements Listener {
    private static JavaPlugin plugin;
    private static boolean dropOnDeath = true;
    private static final Map<UUID, Map<Adornment.Slot, ItemStack>> WORN = new HashMap<>();

    private Adornments() {}

    public static void register(JavaPlugin plugin) {
        Adornments.plugin = plugin;
        dropOnDeath = plugin.getConfig().getBoolean("adornments.drop-on-death", true);
        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new Adornments(), plugin);
        pm.registerEvents(new AdornmentEffects(plugin), plugin);
        pm.registerEvents(new AdornmentLoot(), plugin);
        pm.registerEvents(new AdornmentPack(), plugin);
        AdornmentScreen.register(plugin);
    }

    /** The adornments' ids, for the config. */
    public static Set<String> ids() {
        Set<String> ids = new HashSet<>();
        for (Adornment adornment : Adornment.values()) ids.add(adornment.id());
        return ids;
    }

    private static org.bukkit.NamespacedKey key(Adornment.Slot slot) {
        return Keys.key("adornment_" + slot.id());
    }

    private static Map<Adornment.Slot, ItemStack> worn(Player player) {
        return WORN.computeIfAbsent(player.getUniqueId(), id -> {
            Map<Adornment.Slot, ItemStack> items = new EnumMap<>(Adornment.Slot.class);
            for (Adornment.Slot slot : Adornment.Slot.values()) {
                byte[] data = player.getPersistentDataContainer().get(key(slot), PersistentDataType.BYTE_ARRAY);
                if (data == null) continue;
                try {
                    items.put(slot, ItemStack.deserializeBytes(data));
                } catch (RuntimeException e) {
                    plugin.getLogger().severe("Could not read the " + slot.id() + " of " + player.getName() + ": " + e);
                }
            }
            return items;
        });
    }

    /** A copy of the item in a slot, or null. */
    public static @Nullable ItemStack get(Player player, Adornment.Slot slot) {
        ItemStack item = worn(player).get(slot);
        return item == null ? null : item.clone();
    }

    /// Puts an item in a slot, or empties it. Doesn't touch what was there: the caller has already taken it.
    static void set(Player player, Adornment.Slot slot, @Nullable ItemStack item) {
        if (item == null || item.isEmpty()) {
            worn(player).remove(slot);
            player.getPersistentDataContainer().remove(key(slot));
        } else {
            worn(player).put(slot, item.clone());
            player.getPersistentDataContainer().set(key(slot), PersistentDataType.BYTE_ARRAY, item.serializeAsBytes());
        }
    }

    /** Wearing it, and it isn't disabled in the config. */
    public static boolean wears(Player player, Adornment adornment) {
        return Adornment.of(worn(player).get(adornment.slot())) == adornment && ItemPower.enabled(adornment.id());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (!dropOnDeath || event.getKeepInventory()) return;
        Player player = event.getPlayer();
        for (Adornment.Slot slot : Adornment.Slot.values()) {
            ItemStack item = get(player, slot);
            if (item == null) continue;
            set(player, slot, null);
            if (!item.containsEnchantment(Enchantment.VANISHING_CURSE)) event.getDrops().add(item);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        WORN.remove(event.getPlayer().getUniqueId());
    }
}
