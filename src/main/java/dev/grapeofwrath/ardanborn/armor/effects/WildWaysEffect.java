package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Boots: thorns don't catch you, and grass speeds you.
public final class WildWaysEffect extends PieceEffect implements Listener {
    private static final NamespacedKey KEY = Keys.key("wild_ways");

    public WildWaysEffect(String setId) {
        super("wild_ways", "Wild Ways", setId, EquipmentSlot.FEET, OriginCheck.BEORNING);
    }

    @Override
    protected List<String> lines() {
        return List.of("No berry-bush harm, faster on grass", "Beornings: faster still at night");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        boolean grass = Effects.onBlock(player, m -> m == Material.GRASS_BLOCK);
        Effects.scale(player, Attribute.MOVEMENT_SPEED, KEY, !grass ? 0
                : OriginAbilities.isNight(player.getWorld()) && own(player) ? 0.10 : 0.05);
    }

    @Override
    public void onUnequip(Player player) {
        Effects.scale(player, Attribute.MOVEMENT_SPEED, KEY, 0);
    }

    @EventHandler(ignoreCancelled = true)
    public void onThorns(EntityDamageByBlockEvent event) {
        if (event.getDamager() != null && event.getDamager().getType() == Material.SWEET_BERRY_BUSH
                && event.getEntity() instanceof Player player && worn(player)) event.setCancelled(true);
    }
}
