package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/// Andúril, Flame of the West: it burns the dead, and shines brightest among companions.
public final class Anduril extends RaceItem implements Listener {
    public Anduril() {
        super("anduril", "Andúril", Template.of(RaceTemplate.STAR_OF_ELENDIL),
                OriginCheck.EDAIN, SWORDS);
    }

    private static final int BURN_TICKS = 80;
    private static final int MOST_ALLIES = 3;

    @Override
    protected List<String> lines() {
        return List.of("The undead burn at its stroke", "Mightier in company");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Edain: mightier still");
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.swingAttacker(event);
        if (player == null || !wields(player) || !(event.getEntity() instanceof LivingEntity victim)) return;
        if (OriginAbilities.UNDEAD.contains(victim.getType())) victim.setFireTicks(Math.max(victim.getFireTicks(), BURN_TICKS));
        int allies = Effects.company(player, 12).size() - 1 + (own(player) ? 1 : 0);
        event.setDamage(event.getDamage() * ItemPower.bonus(id, 0.05 * Math.min(MOST_ALLIES, allies)));
    }
}
