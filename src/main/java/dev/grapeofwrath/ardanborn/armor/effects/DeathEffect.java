package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.mounts.Riding;
import dev.grapeofwrath.ardanborn.mounts.RohirrimDamage;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Full set: each monster slain from the saddle adds to melee damage for a while.
public final class DeathEffect extends PieceEffect implements Listener {
    private static final double PER_KILL = 0.04;
    private static final long LAST_MS = 10_000;

    private record Fury(int kills, long until) {}

    private final Map<UUID, Fury> furies = new HashMap<>();

    public DeathEffect(String setId) {
        super("death", "Death!", setId, null, OriginCheck.ROHIRRIM);
        RohirrimDamage.add(this::bonus);
    }

    @Override
    protected List<String> lines() {
        return List.of("Each foe slain from the saddle", "makes your blows the heavier", "Rohirrim: heavier still");
    }

    private double bonus(Player player) {
        Fury fury = furies.get(player.getUniqueId());
        if (fury == null || fury.until() < System.currentTimeMillis() || !worn(player)) return 0;
        return ItemPower.bonus(id, Math.min(own(player) ? 0.28 : 0.2, fury.kills() * PER_KILL)) - 1;
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null || !(event.getEntity() instanceof Enemy) || !Riding.mounted(player) || !worn(player)) return;
        long now = System.currentTimeMillis();
        Fury before = furies.get(player.getUniqueId());
        int kills = before == null || before.until() < now ? 1 : before.kills() + 1;
        furies.put(player.getUniqueId(), new Fury(kills, now + LAST_MS));
    }

    @Override
    public void onUnequip(Player player) {
        furies.remove(player.getUniqueId());
    }
}
