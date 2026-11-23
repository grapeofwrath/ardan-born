package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Full set: strike harder just after coming down from the air.
public final class WindlordEffect extends PieceEffect implements Listener {
    private static final class Flight {
        float fallen;
        boolean glided;
        long until;
    }

    private final Map<UUID, Flight> flights = new HashMap<>();

    public WindlordEffect(String setId) {
        super("windlord", "Windlord", setId, null, OriginCheck.GREAT_EAGLE);
    }

    @Override
    protected List<String> lines() {
        return List.of("Strike harder just after landing", "Eagles: for longer");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        Flight flight = flights.computeIfAbsent(player.getUniqueId(), _ -> new Flight());
        if (!player.isOnGround()) {
            flight.fallen = Math.max(flight.fallen, player.getFallDistance());
            flight.glided |= player.isGliding();
            return;
        }
        if (flight.glided || flight.fallen >= 3) {
            flight.until = System.currentTimeMillis() + (own(player) ? 5_000 : 3_000);
        }
        flight.fallen = 0;
        flight.glided = false;
    }

    @Override
    public void onUnequip(Player player) {
        flights.remove(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = Effects.swingAttacker(event);
        if (player == null) return;
        Flight flight = flights.get(player.getUniqueId());
        if (flight != null && System.currentTimeMillis() < flight.until && worn(player)) {
            event.setDamage(event.getDamage() * ItemPower.bonus(id, 0.15));
        }
    }
}
