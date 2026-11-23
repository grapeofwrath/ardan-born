package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

/// Hadhafang, the throng-cleaver: each kill restores you, and Peredhil strike harder from the saddle.
public final class Hadhafang extends RaceItem implements Listener {
    public Hadhafang() {
        super("hadhafang", "Hadhafang", Template.of(RaceTemplate.EVENSTAR),
                OriginCheck.PEREDHIL, SWORDS);
    }

    @Override
    protected List<String> lines() {
        return List.of("Kills heal you");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Peredhil: extra damage while riding");
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player != null && wields(player)) Effects.heal(player, ItemPower.amount(id, 2));
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.swingAttacker(event);
        if (player != null && wields(player) && player.getVehicle() instanceof LivingEntity && own(player)) {
            event.setDamage(event.getDamage() * ItemPower.bonus(id, 0.15));
        }
    }
}
