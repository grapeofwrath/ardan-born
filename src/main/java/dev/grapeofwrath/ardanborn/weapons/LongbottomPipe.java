package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.rings.RingBearer;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// A pipe of Longbottom Leaf, shared with friends: it mends, and lightens the Shadow.
public final class LongbottomPipe extends Artifact {
    public LongbottomPipe() {
        super("longbottom_pipe", "Longbottom Pipe", Template.of(RaceTemplate.MATHOM),
                OriginCheck.HOBBIT, Material.STICK);
    }

    @Override
    protected List<String> lines() {
        return List.of("Right-click: heals you and nearby", "players, and eases Shadow");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Hobbits: eases more Shadow");
    }

    @Override
    protected long cooldownMs(Player player) {
        return 300_000;
    }

    @Override
    protected void use(Player player) {
        double shadow = own(player) ? 10 : 5;
        for (Player each : Effects.company(player, 6)) {
            Effects.potion(each, PotionEffectType.REGENERATION, 200, 0);
            RingBearer.addShadow(each, -shadow);
        }
        player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getEyeLocation(), 12, 0.3, 0.2, 0.3, 0.01);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CAMPFIRE_CRACKLE, 1f, 1f);
    }
}
