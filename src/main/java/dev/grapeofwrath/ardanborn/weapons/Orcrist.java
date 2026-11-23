package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Illager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Orcrist, the Goblin-cleaver of Gondolin: illagers and piglins fear it.
public final class Orcrist extends RaceItem implements Listener {
    public Orcrist() {
        super("orcrist", "Orcrist", Template.of(RaceTemplate.LIGHT_OF_THE_TREES),
                OriginCheck.NOLDOR, SWORDS);
    }

    @Override
    protected List<String> lines() {
        return List.of("Extra damage to illagers and piglins");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Noldor: also weakens them");
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.swingAttacker(event);
        if (player == null || !wields(player) || !(event.getEntity() instanceof LivingEntity victim)) return;
        EntityType type = victim.getType();
        if (!(victim instanceof Illager) && type != EntityType.PIGLIN && type != EntityType.PIGLIN_BRUTE
                && type != EntityType.ZOMBIFIED_PIGLIN) return;
        event.setDamage(event.getDamage() * ItemPower.bonus(id, 0.20));
        if (own(player)) Effects.potion(victim, PotionEffectType.WEAKNESS, 60, 0);
    }
}
