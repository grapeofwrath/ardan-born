package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/// Chestplate: sorcery does less to you.
public final class SpellWardEffect extends PieceEffect implements Listener {
    private static final Set<EntityDamageEvent.DamageCause> SORCERY = EnumSet.of(EntityDamageEvent.DamageCause.MAGIC,
            EntityDamageEvent.DamageCause.WITHER, EntityDamageEvent.DamageCause.DRAGON_BREATH);

    public SpellWardEffect(String setId) {
        super("spell_ward", "Spell-ward", setId, EquipmentSlot.CHEST, OriginCheck.NOLDOR);
    }

    @Override
    protected List<String> lines() {
        return List.of("Magic and Wither do less hurt", "Noldor: and less again");
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        // Lingering potions and dragon's breath: Paper gives a cloud's damage the cause ENTITY_ATTACK.
        boolean sorcery = SORCERY.contains(event.getCause())
                || event.getDamageSource().getDirectEntity() instanceof AreaEffectCloud;
        if (!sorcery || !(event.getEntity() instanceof Player player) || !worn(player)) return;
        event.setDamage(event.getDamage() * ItemPower.reduction(id, own(player) ? 0.30 : 0.20));
    }
}
