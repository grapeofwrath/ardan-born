package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/// Leggings: keep running and you quicken.
public final class StridersGaitEffect extends PieceEffect implements Listener {
    private final Map<UUID, Long> since = new HashMap<>();

    public StridersGaitEffect(String setId) {
        super("striders_gait", "Strider's Gait", setId, EquipmentSlot.LEGS, OriginCheck.EDAIN);
    }

    @Override
    protected List<String> lines() {
        return List.of("Sprint on, and you have Speed", "Edain: sooner");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        if (!player.isSprinting()) {
            since.remove(player.getUniqueId());
            return;
        }
        long started = since.computeIfAbsent(player.getUniqueId(), _ -> System.currentTimeMillis());
        if (every(10) && System.currentTimeMillis() - started >= (own(player) ? 3_000 : 5_000)) {
            Effects.potion(player, PotionEffectType.SPEED, 20, 0);
        }
    }

    @Override
    public void onUnequip(Player player) {
        since.remove(player.getUniqueId());
    }
}
