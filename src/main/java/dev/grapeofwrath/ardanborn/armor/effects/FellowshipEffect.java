package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/// Full set: you and those beside you take less harm. Two wearers don't add up.
public final class FellowshipEffect extends PieceEffect implements Listener {
    public FellowshipEffect(String setId) {
        super("fellowship", "Fellowship", setId, null, OriginCheck.HOBBIT);
    }

    @Override
    protected List<String> lines() {
        return List.of("You and players near you take less hurt", "Hobbits: and less again");
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim) || Effects.UNSTOPPABLE.contains(event.getCause())) return;
        double best = 1;
        for (Player each : Effects.company(victim, 8)) {
            if (worn(each)) best = Math.min(best, ItemPower.reduction(id, own(each) ? 0.12 : 0.10));
        }
        if (best < 1) event.setDamage(event.getDamage() * best);
    }
}
