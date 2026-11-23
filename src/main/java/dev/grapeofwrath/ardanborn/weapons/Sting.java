package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/// Sting, the spiders' bane. A Hobbit's unlooked-for blow with it goes deeper.
public final class Sting extends RaceItem implements Listener {
    public Sting() {
        super("sting", "Sting", Template.of(RaceTemplate.MATHOM),
                OriginCheck.HOBBIT, SWORDS);
    }

    @Override
    protected List<String> lines() {
        return List.of("Bitter to spiders");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Hobbits: and to the unwary");
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.swingAttacker(event);
        if (player == null || !wields(player)) return;
        double factor = 1;
        // Cave spiders are spiders too.
        if (event.getEntity() instanceof Spider) factor *= ItemPower.bonus(id, 0.25);
        if (event.getEntity() instanceof Mob mob && mob.getTarget() != player && own(player)) factor *= ItemPower.bonus(id, 0.25);
        if (factor != 1) event.setDamage(event.getDamage() * factor);
    }
}
