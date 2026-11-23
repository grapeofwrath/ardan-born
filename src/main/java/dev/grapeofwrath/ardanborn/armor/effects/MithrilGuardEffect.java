package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.CustomArmor;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Chestplate (the Mithril Coat): it turned the troll's spear in Moria. Any single hit over 4 hearts is
/// blunted; Hobbits, for whom it was made, are blunted more and not knocked back by it.
public final class MithrilGuardEffect implements SetEffect, Listener {
    private static final double BIG_HIT = 8.0; // 4 hearts, after armor
    private static final double REDUCTION = 0.30;
    private static final double HOBBIT_REDUCTION = 0.40;

    private final String setId;
    /// The server tick each Hobbit's last big hit landed, so that hit's knockback can be stopped.
    private final Map<UUID, Integer> bigHitTick = new HashMap<>();

    public MithrilGuardEffect(String setId) {
        this.setId = setId;
    }

    private static final String EFFECT_ID = "mithril";

    @Override
    public String id() {
        return EFFECT_ID;
    }

    @Override
    public String name() {
        return "Mithril";
    }

    @Override
    public List<Component> describe(ArmorTier tier) {
        return List.of(
                Component.text("Heavy blows are softened"),
                Component.text("Hobbits: the more, and do not throw you back"));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (Effects.UNSTOPPABLE.contains(event.getCause()) || event.getFinalDamage() <= BIG_HIT) return;
        if (!ItemPower.enabled(EFFECT_ID) || CustomArmor.wornTier(player, EquipmentSlot.CHEST, setId) == null) return;
        boolean hobbit = OriginCheck.is(player, OriginCheck.HOBBIT);
        event.setDamage(event.getDamage() * ItemPower.reduction(EFFECT_ID, hobbit ? HOBBIT_REDUCTION : REDUCTION));
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().add(0, 1.1, 0), 12, 0.3, 0.3, 0.3, 0.05);
        if (hobbit) bigHitTick.put(player.getUniqueId(), Bukkit.getCurrentTick());
    }

    @EventHandler(ignoreCancelled = true)
    public void onKnockback(EntityKnockbackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Integer tick = bigHitTick.get(player.getUniqueId());
        if (tick != null && tick == Bukkit.getCurrentTick()) event.setCancelled(true);
    }
}
