package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.mounts.Riding;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Helmet: in the saddle, arrows and their like do less. A Rohirrim's mount is guarded too.
public final class HorsetailCrestEffect extends PieceEffect implements Listener {
    public HorsetailCrestEffect(String setId) {
        super("horsetail_crest", "Horsetail Crest", setId, EquipmentSlot.HEAD, OriginCheck.ROHIRRIM);
    }

    @Override
    protected List<String> lines() {
        return List.of("Riding, arrows harm you less", "Rohirrim: and your mount also");
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectile(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) return;
        boolean guarded = event.getEntity() instanceof Player player
                ? Riding.mounted(player) && worn(player)
                : event.getEntity() instanceof AbstractHorse mount && mount.getPassengers().stream()
                        .anyMatch(rider -> rider instanceof Player p && worn(p) && own(p));
        if (guarded) event.setDamage(event.getDamage() * ItemPower.reduction(id, 0.2));
    }
}
