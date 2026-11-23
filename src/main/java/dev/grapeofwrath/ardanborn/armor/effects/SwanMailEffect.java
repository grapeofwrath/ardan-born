package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Chestplate: water and rain shield you. Teleri slowly heal there.
public final class SwanMailEffect extends PieceEffect implements Listener {
    public SwanMailEffect(String setId) {
        super("swan_mail", "Swan-mail", setId, EquipmentSlot.CHEST, OriginCheck.TELERI);
    }

    @Override
    protected List<String> lines() {
        return List.of("Tougher in water and rain", "Teleri: slowly heal there");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        if (every(100) && player.isInWaterOrRain() && own(player)) Effects.heal(player, ItemPower.amount(id, 1));
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || !player.isInWaterOrRain() || !worn(player)) return;
        if (Effects.UNSTOPPABLE.contains(event.getCause())) return;
        event.setDamage(event.getDamage() * ItemPower.reduction(id, 0.12));
    }
}
