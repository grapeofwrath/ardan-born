package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// The skin-changer's claw: for a little while, something of the bear is in you.
public final class SkinChangersClaw extends Artifact {
    public SkinChangersClaw() {
        super("skin_changers_claw", "Skin-changer's Claw", Template.HONEY,
                OriginCheck.BEORNING, Material.FLINT);
    }

    private static final NamespacedKey KEY = Keys.key("skin_changers_claw");
    private static final int TICKS = 160;
    private static final int NIGHT_TICKS = 240;

    private final Map<UUID, Long> until = new HashMap<>();

    @Override
    protected List<String> lines() {
        return List.of("Right-click: hit harder, run faster");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Beornings: for longer at night");
    }

    @Override
    protected long cooldownMs(Player player) {
        return 90_000;
    }

    @Override
    protected void use(Player player) {
        int ticks = own(player) && OriginAbilities.isNight(player.getWorld()) ? NIGHT_TICKS : TICKS;
        until.put(player.getUniqueId(), System.currentTimeMillis() + ticks * 50L);
        Effects.scale(player, Attribute.MOVEMENT_SPEED, KEY, 0.1);
        Bukkit.getScheduler().runTaskLater(Effects.plugin(),
                () -> Effects.scale(player, Attribute.MOVEMENT_SPEED, KEY, 0), ticks);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_POLAR_BEAR_WARNING, 1f, 0.8f);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.swingAttacker(event);
        if (player == null || System.currentTimeMillis() >= until.getOrDefault(player.getUniqueId(), 0L)) return;
        event.setDamage(event.getDamage() * ItemPower.bonus(id, 0.20));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        until.remove(event.getPlayer().getUniqueId());
    }
}
