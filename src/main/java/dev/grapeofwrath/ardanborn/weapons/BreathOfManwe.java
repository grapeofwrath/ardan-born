package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// The Breath of Manwë, lord of the winds: a gale that throws monsters back and bears friends up.
public final class BreathOfManwe extends Artifact {
    public BreathOfManwe() {
        super("breath_of_manwe", "Breath of Manwë", Template.FEATHER,
                OriginCheck.GREAT_EAGLE, Material.BREEZE_ROD);
    }

    @Override
    protected List<String> lines() {
        return List.of("Right-click: throw monsters back,", "and friends fall slowly");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Eagles: reaches further");
    }

    @Override
    protected long cooldownMs(Player player) {
        return 45_000;
    }

    @Override
    protected void use(Player player) {
        double range = own(player) ? 12 : 8;
        for (LivingEntity mob : Effects.enemiesNear(player, range)) Effects.push(mob, player.getLocation(), 1.6);
        for (Player each : Effects.company(player, range)) Effects.potion(each, PotionEffectType.SLOW_FALLING, 200, 0);
        player.getWorld().spawnParticle(Particle.GUST_EMITTER_LARGE, player.getLocation().add(0, 1, 0), 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1.2f, 0.8f);
    }
}
