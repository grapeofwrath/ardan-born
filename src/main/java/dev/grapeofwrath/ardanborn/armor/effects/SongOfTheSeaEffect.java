package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/// Full set: blows struck in water or rain sometimes heal the company.
public final class SongOfTheSeaEffect extends PieceEffect implements Listener {
    private final OriginAbilities.Cooldown cooldown = new OriginAbilities.Cooldown();

    public SongOfTheSeaEffect(String setId) {
        super("song_of_the_sea", "Song of the Sea", setId, null, OriginCheck.TELERI);
    }

    @Override
    protected List<String> lines() {
        return List.of("Hits in water or rain sometimes heal", "you and nearby players", "Teleri: more often");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = event.getDamager() instanceof Trident trident && trident.getShooter() instanceof Player thrower
                ? thrower : Effects.meleeAttacker(event);
        if (player == null || !(event.getEntity() instanceof LivingEntity)) return;
        if (!player.isInWaterOrRain() || !worn(player)) return;
        if (!ItemPower.roll(id, own(player) ? 0.25 : 0.15)) return;
        if (!cooldown.tryUseQuiet(player, LoreWiseEffect.shorten(player, 3_000))) return;
        for (Player each : Effects.company(player, 6)) {
            Effects.heal(each, ItemPower.amount(id, 2));
            each.getWorld().spawnParticle(Particle.HEART, each.getLocation().add(0, 1.8, 0), 2, 0.3, 0.2, 0.3, 0);
        }
    }
}
