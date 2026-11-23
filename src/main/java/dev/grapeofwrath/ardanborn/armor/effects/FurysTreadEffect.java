package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Boots: each kill speeds you on. Uruk-hai in Battle-fury can't be thrown back.
public final class FurysTreadEffect extends PieceEffect implements Listener {
    public FurysTreadEffect(String setId) {
        super("furys_tread", "Fury's Tread", setId, EquipmentSlot.FEET, OriginCheck.URUK_HAI);
    }

    @Override
    protected List<String> lines() {
        return List.of("Each slaying brings Speed", "Uruk-hai: unmoved in Battle-fury");
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player != null && worn(player)) Effects.potion(player, PotionEffectType.SPEED, 80, 0);
    }

    @EventHandler(ignoreCancelled = true)
    public void onKnockback(EntityKnockbackEvent event) {
        if (event.getEntity() instanceof Player player && OriginAbilities.BattleFury.active(player)
                && worn(player) && own(player)) event.setCancelled(true);
    }
}
