package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Full set: brought low, you rally the company.
public final class LastAllianceEffect extends PieceEffect implements Listener {
    private final OriginAbilities.Cooldown cooldown = new OriginAbilities.Cooldown();

    public LastAllianceEffect(String setId) {
        super("last_alliance", "Last Alliance", setId, null, OriginCheck.EDAIN);
    }

    @Override
    protected List<String> lines() {
        return List.of("Near death, you and players near you", "have Strength and Speed", "Edain: ready again sooner");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        double left = player.getHealth() - event.getFinalDamage();
        if (left <= 0 || left >= Effects.maxHealth(player) * 0.3 || !worn(player)) return;
        if (!cooldown.tryUseQuiet(player, LoreWiseEffect.shorten(player, own(player) ? 90_000 : 120_000))) return;
        for (Player each : Effects.company(player, 8)) {
            Effects.potion(each, PotionEffectType.STRENGTH, 160, 0);
            Effects.potion(each, PotionEffectType.SPEED, 160, 0);
        }
        player.getWorld().playSound(player.getLocation(), Sound.EVENT_RAID_HORN, 1f, 1.2f);
    }
}
