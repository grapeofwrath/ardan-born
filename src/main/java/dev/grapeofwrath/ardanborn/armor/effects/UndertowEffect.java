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
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Leggings: melee attackers are sometimes swept back.
public final class UndertowEffect extends PieceEffect implements Listener {
    public UndertowEffect(String setId) {
        super("undertow", "Undertow", setId, EquipmentSlot.LEGS, OriginCheck.TELERI);
    }

    @Override
    protected List<String> lines() {
        return List.of("Sometimes sweeps attackers back", "Teleri: more often");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Mob mob = Effects.meleeMob(event);
        if (mob == null || !(event.getEntity() instanceof Player player) || !worn(player)) return;
        if (!ItemPower.roll(id, own(player) ? 0.25 : 0.15)) return;
        Effects.push(mob, player.getLocation(), 0.9);
        Effects.potion(mob, PotionEffectType.SLOWNESS, 60, 0);
        mob.getWorld().spawnParticle(Particle.SPLASH, mob.getLocation().add(0, 0.5, 0), 30, 0.4, 0.4, 0.4, 0.1);
    }
}
