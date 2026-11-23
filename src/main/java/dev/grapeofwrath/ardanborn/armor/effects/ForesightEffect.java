package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Helmet: one hit in a while is seen coming, and halved.
public final class ForesightEffect extends PieceEffect implements Listener {
    /// Scratches don't use it up.
    private static final double SMALLEST = 2.0;

    private final OriginAbilities.Cooldown cooldown = new OriginAbilities.Cooldown();

    public ForesightEffect(String setId) {
        super("foresight", "Foresight", setId, EquipmentSlot.HEAD, OriginCheck.PEREDHIL);
    }

    @Override
    protected List<String> lines() {
        return List.of("Now and then, softens a hit", "Peredhil: more often");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getFinalDamage() < SMALLEST || !worn(player)) return;
        if (!cooldown.tryUseQuiet(player, LoreWiseEffect.shorten(player, own(player) ? 30_000 : 45_000))) return;
        event.setDamage(event.getDamage() * ItemPower.reduction(id, 0.5));
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1.2, 0), 12, 0.4, 0.5, 0.4, 0.02);
    }
}
