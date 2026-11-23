package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Hithlain, the grey rope of Lórien: it holds fast where it's thrown, and draws you there.
public final class Hithlain extends Artifact {
    public Hithlain() {
        super("hithlain", "Hithlain", Template.of(RaceTemplate.MALLORN_LEAF),
                OriginCheck.SINDAR, Material.LEAD);
    }

    /// How long after a pull a fall does no harm.
    private static final long SAFE_MS = 4_000;

    private final Map<UUID, Long> safeUntil = new HashMap<>();

    @Override
    protected List<String> lines() {
        return List.of("Right-click a block: pulls you to it");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Sindar: further, and more often");
    }

    @Override
    protected long cooldownMs(Player player) {
        return own(player) ? 6_000 : 10_000;
    }

    private @Nullable Location anchor(Player player) {
        RayTraceResult hit = player.rayTraceBlocks(own(player) ? 24 : 16, FluidCollisionMode.NEVER);
        return hit == null ? null : hit.getHitPosition().toLocation(player.getWorld());
    }

    @Override
    protected boolean ready(Player player) {
        if (player.isInsideVehicle()) return false;
        if (anchor(player) != null) return true;
        player.sendActionBar(Component.text("Nothing in reach", NamedTextColor.GRAY));
        return false;
    }

    @Override
    protected void use(Player player) {
        Location anchor = anchor(player);
        if (anchor == null) return;
        Vector pull = anchor.toVector().subtract(player.getLocation().toVector());
        double distance = pull.length();
        if (distance < 0.5) return;
        // Enough speed to arrive against drag, and a lift against gravity.
        Vector velocity = pull.normalize().multiply(Math.min(2.2, 0.35 + distance * 0.085));
        player.setVelocity(velocity.setY(Math.min(1.6, velocity.getY() + 0.35)));
        player.setFallDistance(0);
        safeUntil.put(player.getUniqueId(), System.currentTimeMillis() + SAFE_MS);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 0.8f, 1.4f);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFall(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL || !(event.getEntity() instanceof Player player)) return;
        Long until = safeUntil.remove(player.getUniqueId());
        if (until != null && System.currentTimeMillis() < until) event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        safeUntil.remove(event.getPlayer().getUniqueId());
    }
}
