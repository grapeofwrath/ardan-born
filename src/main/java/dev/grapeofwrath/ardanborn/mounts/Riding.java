package dev.grapeofwrath.ardanborn.mounts;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/// Who is riding, and for how long they have been galloping.
public final class Riding implements Listener {
    /// Blocks per tick (4 blocks a second) at which a mount counts as galloping.
    private static final double GALLOP = 0.2;
    /// A longer move in one tick is a teleport.
    private static final double TELEPORT = 4;
    /// Slower than a gallop for this long, and the gallop is over.
    private static final int GRACE_TICKS = 10;

    private static final class Ride {
        private Location last;
        private int since = -1;
        private int lastFast;
    }

    private static final Map<UUID, Ride> RIDES = new HashMap<>();

    private Riding() {}

    public static void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new Riding(), plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) track(player);
        }, 1L, 1L);
    }

    /// The horse, donkey, mule, camel or llama the player is riding, or null.
    public static @Nullable AbstractHorse mount(Player player) {
        return player.getVehicle() instanceof AbstractHorse mount ? mount : null;
    }

    public static boolean mounted(Player player) {
        return mount(player) != null;
    }

    /// How many ticks the player's mount has been galloping; 0 when it isn't.
    public static int gallopTicks(Player player) {
        Ride ride = RIDES.get(player.getUniqueId());
        return ride == null || ride.since < 0 || !mounted(player) ? 0 : Bukkit.getCurrentTick() - ride.since + 1;
    }

    /// Starts the gallop's time anew.
    public static void restartGallop(Player player) {
        Ride ride = RIDES.get(player.getUniqueId());
        if (ride != null) ride.since = -1;
    }

    private static void track(Player player) {
        AbstractHorse mount = mount(player);
        if (mount == null) {
            RIDES.remove(player.getUniqueId());
            return;
        }
        Ride ride = RIDES.computeIfAbsent(player.getUniqueId(), _ -> new Ride());
        Location now = mount.getLocation();
        int tick = Bukkit.getCurrentTick();
        if (ride.last != null && ride.last.getWorld() == now.getWorld()) {
            double moved = Math.hypot(now.getX() - ride.last.getX(), now.getZ() - ride.last.getZ());
            if (moved >= GALLOP && moved < TELEPORT) {
                ride.lastFast = tick;
                if (ride.since < 0) ride.since = tick;
            } else if (moved >= TELEPORT || tick - ride.lastFast > GRACE_TICKS) {
                ride.since = -1;
            }
        }
        ride.last = now;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        RIDES.remove(event.getPlayer().getUniqueId());
    }
}
