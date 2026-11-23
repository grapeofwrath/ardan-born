package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/// Full set: an arrow sometimes strikes a second foe.
public final class GaladhrimVolleyEffect extends PieceEffect implements Listener {
    public GaladhrimVolleyEffect(String setId) {
        super("galadhrim_volley", "Galadhrim Volley", setId, null, OriginCheck.SINDAR);
    }

    @Override
    protected List<String> lines() {
        return List.of("Arrow hits sometimes strike", "a second foe", "Sindar: more often");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArrow(EntityDamageByEntityEvent event) {
        Player player = Effects.archer(event);
        if (player == null || !worn(player) || !ItemPower.roll(id, own(player) ? 0.25 : 0.15)) return;
        Location from = event.getEntity().getLocation();
        LivingEntity second = null;
        for (LivingEntity near : Effects.enemiesNear(from, 8)) {
            if (near == event.getEntity()) continue;
            if (second == null || near.getLocation().distanceSquared(from) < second.getLocation().distanceSquared(from)) second = near;
        }
        if (second == null) return;
        second.getWorld().spawnParticle(Particle.CRIT, second.getLocation().add(0, 1, 0), 12, 0.3, 0.4, 0.3, 0.1);
        Effects.strike(second, ItemPower.amount(id, event.getFinalDamage() * 0.5), player);
    }
}
