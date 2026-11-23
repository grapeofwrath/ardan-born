package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Helmet: the hostile creatures you look at are marked. Eagles also see in the dark while high in the sky.
///
/// The helmet is a spyglass (see [dev.grapeofwrath.ardanborn.armor.sets.ThorondorSet]), so it also zooms
/// when held and used. That part is vanilla and needs no code.
public final class HawkSightEffect implements SetEffect {
    private static final int RANGE = 32;
    private static final int HIGH_ALTITUDE = 100;
    /// Look for a target every this many effect ticks (each is 2 game ticks).
    private static final int EVERY = 5;

    private final Map<UUID, Integer> counters = new HashMap<>();

    private static final String EFFECT_ID = "hawk_sight";

    @Override
    public String id() {
        return EFFECT_ID;
    }

    @Override
    public String name() {
        return "Hawk-sight";
    }

    @Override
    public List<Component> describe(ArmorTier tier) {
        return List.of(
                Component.text("Monsters you look at glow"),
                Component.text("Use in hand to zoom"),
                Component.text("Eagles: see in the dark up high"));
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        int count = counters.merge(player.getUniqueId(), 1, Integer::sum);
        if (count % EVERY != 0) return;

        Entity target = player.getTargetEntity(RANGE);
        if (target instanceof Monster monster) {
            monster.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0, true, false, false));
        }
        if (player.getLocation().getY() > HIGH_ALTITUDE && !player.isOnGround()
                && OriginCheck.is(player, OriginCheck.GREAT_EAGLE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 320, 0, true, false, true));
        }
    }

    @Override
    public void onUnequip(Player player) {
        counters.remove(player.getUniqueId());
    }
}
