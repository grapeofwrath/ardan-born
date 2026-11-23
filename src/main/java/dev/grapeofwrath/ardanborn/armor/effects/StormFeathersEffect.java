package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Particle;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Chestplate: melee attackers are sometimes buffeted back.
public final class StormFeathersEffect extends PieceEffect implements Listener {
    public StormFeathersEffect(String setId) {
        super("storm_feathers", "Storm-feathers", setId, EquipmentSlot.CHEST, OriginCheck.GREAT_EAGLE);
    }

    @Override
    protected List<String> lines() {
        return List.of("Sometimes pushes attackers back", "Eagles: more often");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Mob mob = Effects.meleeMob(event);
        if (mob == null || !(event.getEntity() instanceof Player player) || !worn(player)) return;
        if (!ItemPower.roll(id, own(player) ? 0.25 : 0.15)) return;
        Effects.push(mob, player.getLocation(), 1.2);
        mob.getWorld().spawnParticle(Particle.GUST, mob.getLocation().add(0, 1, 0), 1);
    }
}
