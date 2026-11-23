package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Full set: every few blows sweep the foes around.
public final class MaulEffect extends PieceEffect implements Listener {
    private final Map<UUID, Integer> hits = new HashMap<>();

    public MaulEffect(String setId) {
        super("maul", "Maul", setId, null, OriginCheck.BEORNING);
    }

    @Override
    protected List<String> lines() {
        return List.of("Every few hits also strike nearby foes", "Beornings: more often at night");
    }

    @Override
    public void onUnequip(Player player) {
        hits.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.meleeAttacker(event);
        if (player == null || !(event.getEntity() instanceof LivingEntity victim) || !worn(player)) return;
        int every = OriginAbilities.isNight(player.getWorld()) && own(player) ? 3 : 4;
        if (hits.merge(player.getUniqueId(), 1, Integer::sum) < every) return;
        hits.remove(player.getUniqueId());
        double splash = ItemPower.amount(id, event.getFinalDamage() * 0.5);
        for (LivingEntity near : Effects.enemiesNear(victim, 3)) {
            if (near != victim) Effects.strike(near, splash, player);
        }
        victim.getWorld().spawnParticle(Particle.SWEEP_ATTACK, victim.getLocation().add(0, 1, 0), 3, 0.8, 0.2, 0.8, 0);
    }
}
