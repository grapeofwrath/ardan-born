package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Chestplate: heal once you've been out of harm's way for a while.
public final class WhiteTreeEffect extends PieceEffect implements Listener {
    private final Map<UUID, Long> hurt = new HashMap<>();

    public WhiteTreeEffect(String setId) {
        super("white_tree", "White Tree", setId, EquipmentSlot.CHEST, OriginCheck.DUNEDAIN);
    }

    @Override
    protected List<String> lines() {
        return List.of("When harm is past, you heal", "Dúnedain: and heal faster");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) hurt.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        hurt.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        if (!every(own(player) ? 60 : 80)) return;
        if (System.currentTimeMillis() - hurt.getOrDefault(player.getUniqueId(), 0L) >= 8_000) {
            Effects.heal(player, ItemPower.amount(id, 2));
        }
    }
}
