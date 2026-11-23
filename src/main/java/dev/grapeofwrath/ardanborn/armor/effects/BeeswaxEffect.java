package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Sound;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Chestplate: bees sting whoever strikes you.
public final class BeeswaxEffect extends PieceEffect implements Listener {
    public BeeswaxEffect(String setId) {
        super("beeswax", "Beeswax", setId, EquipmentSlot.CHEST, OriginCheck.BEORNING);
    }

    @Override
    protected List<String> lines() {
        return List.of("At times attackers are poisoned", "Beornings: more often");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Mob mob = Effects.meleeMob(event);
        if (mob == null || !(event.getEntity() instanceof Player player) || !worn(player)) return;
        if (!ItemPower.roll(id, own(player) ? 0.15 : 0.10)) return;
        Effects.potion(mob, PotionEffectType.POISON, 80, 0);
        mob.getWorld().playSound(mob.getLocation(), Sound.ENTITY_BEE_STING, 0.8f, 1f);
    }
}
