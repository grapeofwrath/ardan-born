package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.mounts.Riding;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Boots: a galloping mount tramples the monsters it runs into, each once a second.
public final class TramplingEffect extends PieceEffect {
    private static final int PERIOD_TICKS = 20;
    /// When each foe was last trampled, by anyone.
    private final Map<UUID, Integer> trampled = new HashMap<>();

    public TramplingEffect(String setId) {
        super("trampling", "Trampling", setId, EquipmentSlot.FEET, OriginCheck.ROHIRRIM);
    }

    @Override
    protected List<String> lines() {
        return List.of("Your mount at a gallop tramples foes", "Rohirrim: more sorely");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        AbstractHorse mount = Riding.mount(player);
        if (mount == null || Riding.gallopTicks(player) == 0) return;
        int now = Bukkit.getCurrentTick();
        trampled.values().removeIf(tick -> now - tick >= PERIOD_TICKS);
        for (Entity near : mount.getWorld().getNearbyEntities(mount.getBoundingBox().expand(0.5, 0.2, 0.5))) {
            if (!(near instanceof Enemy) || !(near instanceof LivingEntity foe) || foe.isDead()) continue;
            if (near.equals(mount) || mount.getPassengers().contains(near)) continue;
            if (trampled.putIfAbsent(foe.getUniqueId(), now) != null) continue;
            Effects.strike(foe, ItemPower.amount(id, own(player) ? 6 : 4), player);
        }
    }
}
