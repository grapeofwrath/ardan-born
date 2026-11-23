package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Full set: slaying the Enemy's creatures builds to a burst.
public final class WrathOfFingolfinEffect extends PieceEffect implements Listener {
    private final Map<UUID, Integer> kills = new HashMap<>();

    public WrathOfFingolfinEffect(String setId) {
        super("wrath_of_fingolfin", "Wrath of Fingolfin", setId, null, OriginCheck.NOLDOR);
    }

    @Override
    protected List<String> lines() {
        return List.of("Slay undead and Nether creatures", "to charge a bursting blow", "Noldor: charges sooner");
    }

    @Override
    public void onUnequip(Player player) {
        kills.remove(player.getUniqueId());
    }

    private int needed(Player player) {
        return own(player) ? 3 : 5;
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null || Effects.striking() || !worn(player)) return;
        EntityType type = event.getEntityType();
        if (!OriginAbilities.UNDEAD.contains(type) && !OriginAbilities.NETHER.contains(type)) return;
        int count = kills.merge(player.getUniqueId(), 1, Integer::sum);
        if (count == needed(player)) {
            player.sendActionBar(Component.text("The wrath of Fingolfin is upon you", NamedTextColor.GOLD));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.meleeAttacker(event);
        if (player == null || !(event.getEntity() instanceof LivingEntity victim) || victim instanceof Player) return;
        if (kills.getOrDefault(player.getUniqueId(), 0) < needed(player) || !worn(player)) return;
        kills.remove(player.getUniqueId());
        double burst = ItemPower.amount(id, 8);
        event.setDamage(event.getDamage() + burst);
        for (LivingEntity near : Effects.enemiesNear(victim, 5)) {
            if (near != victim) Effects.strike(near, burst, player);
        }
        victim.getWorld().spawnParticle(Particle.END_ROD, victim.getLocation().add(0, 1, 0), 60, 1.5, 0.8, 1.5, 0.1);
        victim.getWorld().playSound(victim.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.5f, 1.6f);
    }
}
