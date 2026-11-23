package dev.grapeofwrath.ardanborn.mounts;

import dev.grapeofwrath.ardanborn.items.Effects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;

/// The Rohirrim's damage bonuses (Charge of the Mark, Spear-hardy, the Spear of the Mark and Death!), which
/// add up instead of multiplying, to no more than a cap from the config.
public final class RohirrimDamage implements Listener {
    private static final List<ToDoubleFunction<Player>> BONUSES = new ArrayList<>();
    private static double cap = 0.6;

    private RohirrimDamage() {}

    public static void register(Plugin plugin) {
        cap = Math.max(0, plugin.getConfig().getDouble("rohirrim.damage-bonus-cap", 0.6));
        Bukkit.getPluginManager().registerEvents(new RohirrimDamage(), plugin);
    }

    /// `bonus` gives the player's present bonus to melee damage, e.g. 0.2 for +20%, or 0.
    public static void add(ToDoubleFunction<Player> bonus) {
        BONUSES.add(bonus);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.swingAttacker(event);
        if (player == null) return;
        double total = 0;
        for (ToDoubleFunction<Player> bonus : BONUSES) total += bonus.applyAsDouble(player);
        if (total > 0) event.setDamage(event.getDamage() * (1 + Math.min(cap, total)));
    }
}
