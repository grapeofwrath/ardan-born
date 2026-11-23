package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Leggings: thrown snowballs and eggs sting, and draw the monster to you.
public final class StonesThrowEffect extends PieceEffect implements Listener {
    public StonesThrowEffect(String setId) {
        super("stones_throw", "Stone's Throw", setId, EquipmentSlot.LEGS, OriginCheck.HOBBIT);
    }

    @Override
    protected List<String> lines() {
        return List.of("Thrown snowballs and eggs hurt", "and draw monsters", "Hobbits: hurt more");
    }

    @EventHandler(ignoreCancelled = true)
    public void onThrow(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Snowball) && !(event.getDamager() instanceof Egg)) return;
        if (!(event.getEntity() instanceof Enemy) || !(event.getEntity() instanceof Mob mob)) return;
        if (!(((Projectile) event.getDamager()).getShooter() instanceof Player player) || !worn(player)) return;
        event.setDamage(Math.max(event.getDamage(), ItemPower.amount(id, own(player) ? 4 : 2)));
        mob.setTarget(player);
    }
}
