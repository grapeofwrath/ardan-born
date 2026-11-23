package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Chestplate: hard to throw back, and blasts do less. Dwarves take less harm in the deep.
public final class UnmovedEffect extends PieceEffect implements Listener {
    private static final NamespacedKey KEY = Keys.key("unmoved");

    public UnmovedEffect(String setId) {
        super("unmoved", "Unmoved", setId, EquipmentSlot.CHEST, OriginCheck.DWARF);
    }

    @Override
    protected List<String> lines() {
        return List.of("Not lightly moved, nor hurt by blasts so sorely", "Dwarves: hardier in the deep");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        Effects.modifier(player, Attribute.KNOCKBACK_RESISTANCE, KEY, 0.4, AttributeModifier.Operation.ADD_NUMBER);
    }

    @Override
    public void onUnequip(Player player) {
        Effects.modifier(player, Attribute.KNOCKBACK_RESISTANCE, KEY, 0, AttributeModifier.Operation.ADD_NUMBER);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || !worn(player)) return;
        if (Effects.UNSTOPPABLE.contains(event.getCause())) return;
        double factor = 1;
        if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            factor *= ItemPower.reduction(id, 0.25);
        }
        if (OriginAbilities.inTheDeep(player) && own(player)) factor *= ItemPower.reduction(id, 0.15);
        if (factor != 1) event.setDamage(event.getDamage() * factor);
    }
}
