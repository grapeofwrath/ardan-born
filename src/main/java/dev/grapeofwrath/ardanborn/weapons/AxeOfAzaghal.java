package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/// The Axe of Azaghâl, lord of Belegost, who wounded the dragon: it bites deepest into great foes.
public final class AxeOfAzaghal extends RaceItem implements Listener {
    public AxeOfAzaghal() {
        super("axe_of_azaghal", "Axe of Azaghâl", Template.ARKENSTONE,
                OriginCheck.NAUGRIM, List.of(Material.DIAMOND_AXE, Material.NETHERITE_AXE));
    }

    private static final double BIG = 40; // 20 hearts

    @Override
    protected List<String> lines() {
        return List.of("Extra damage to mighty foes");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Naugrim: even more");
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.swingAttacker(event);
        if (player == null || !wields(player) || !(event.getEntity() instanceof LivingEntity victim)) return;
        if (Effects.maxHealth(victim) >= BIG) event.setDamage(event.getDamage() * ItemPower.bonus(id, own(player) ? 0.25 : 0.15));
    }
}
