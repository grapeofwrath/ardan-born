package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.List;

/// The Net of the Havens: cast, it snares the monsters where it falls.
public final class NetOfTheHavens extends Artifact {
    public NetOfTheHavens() {
        super("net_of_the_havens", "Net of the Havens", Template.PEARL,
                OriginCheck.TELERI, Material.LEAD);
    }

    private static final int RANGE = 16;

    @Override
    protected List<String> lines() {
        return List.of("Right-click: snare monsters");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Teleri: the net also hurts them");
    }

    @Override
    protected long cooldownMs(Player player) {
        return 45_000;
    }

    @Override
    protected void use(Player player) {
        RayTraceResult hit = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(),
                RANGE, FluidCollisionMode.NEVER, true, 0.5, entity -> entity instanceof LivingEntity && entity != player);
        Location spot = hit != null ? hit.getHitPosition().toLocation(player.getWorld())
                : player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(RANGE));
        List<LivingEntity> netted = Effects.enemiesNear(spot, 4);
        for (LivingEntity mob : netted) Effects.potion(mob, PotionEffectType.SLOWNESS, 80, 3);
        player.getWorld().spawnParticle(Particle.SPLASH, spot, 120, 2, 0.4, 2, 0.1);
        player.getWorld().playSound(spot, Sound.ENTITY_FISHING_BOBBER_SPLASH, 1f, 0.7f);
        if (netted.isEmpty() || !own(player)) return;
        new BukkitRunnable() {
            int left = 4;

            @Override
            public void run() {
                if (left-- <= 0 || !player.isOnline()) {
                    cancel();
                    return;
                }
                for (LivingEntity mob : netted) {
                    if (mob.isValid()) Effects.strike(mob, ItemPower.amount(id, 1), player);
                }
            }
        }.runTaskTimer(Effects.plugin(), 20L, 20L);
    }
}
