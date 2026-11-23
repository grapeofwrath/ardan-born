package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Boots: the cold of the Grinding Ice can't touch you.
public final class HelcaraxeEffect extends PieceEffect implements Listener {
    private static final NamespacedKey KEY = Keys.key("helcaraxe");

    public HelcaraxeEffect(String setId) {
        super("helcaraxe", "Helcaraxë", setId, EquipmentSlot.FEET, OriginCheck.NOLDOR);
    }

    @Override
    protected List<String> lines() {
        return List.of("No freezing, faster on snow and ice", "Noldor: faster still");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        if (player.getFreezeTicks() > 0) player.setFreezeTicks(0);
        boolean cold = Effects.onBlock(player, m -> Tag.SNOW.isTagged(m) || Tag.ICE.isTagged(m))
                // A thin layer of snow is the block you stand in, not on.
                || player.getLocation().getBlock().getType() == Material.SNOW;
        Effects.scale(player, Attribute.MOVEMENT_SPEED, KEY, !cold ? 0 : own(player) ? 0.20 : 0.10);
    }

    @Override
    public void onUnequip(Player player) {
        Effects.scale(player, Attribute.MOVEMENT_SPEED, KEY, 0);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFreeze(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FREEZE
                && event.getEntity() instanceof Player player && worn(player)) event.setCancelled(true);
    }
}
