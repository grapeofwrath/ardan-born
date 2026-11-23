package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// The Bow of the Galadhrim, as Galadriel gave Legolas: its first full shot at a foe strikes hardest.
public final class BowOfTheGaladhrim extends RaceItem implements Listener {
    public BowOfTheGaladhrim() {
        super("bow_of_the_galadhrim", "Bow of the Galadhrim", Template.of(RaceTemplate.MALLORN_LEAF),
                OriginCheck.SINDAR, List.of(Material.BOW));
    }

    /// Marks full-draw arrows shot from the bow, so their hits are known even after the bow is put away.
    private static final NamespacedKey ARROW = Keys.key("galadhrim_arrow");

    @Override
    protected List<String> lines() {
        return List.of("At full draw, sorest to the unhurt");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Sindar: and slows them");
    }

    @EventHandler(ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (is(event.getBow()) && event.getProjectile() instanceof AbstractArrow arrow
                // Set only on a full draw.
                && arrow.isCritical()) {
            arrow.getPersistentDataContainer().set(ARROW, PersistentDataType.BOOLEAN, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.archer(event);
        if (player == null || !ItemPower.enabled(id) || !event.getDamager().getPersistentDataContainer().has(ARROW)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (victim.getHealth() < Effects.maxHealth(victim) - 0.01) return;
        event.setDamage(event.getDamage() * ItemPower.bonus(id, 0.20));
        if (own(player)) Effects.potion(victim, PotionEffectType.SLOWNESS, 60, 1);
    }
}
