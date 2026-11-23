package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Helmet: no darkness clouds your eyes. Noldor see in the dark of the Nether.
public final class UncloudedEffect extends PieceEffect implements Listener {
    public UncloudedEffect(String setId) {
        super("unclouded", "Unclouded", setId, EquipmentSlot.HEAD, OriginCheck.NOLDOR);
    }

    @Override
    protected List<String> lines() {
        return List.of("Blindness and Darkness touch you not", "Noldor: in the Nether the dark is clear");
    }

    @Override
    public void onEquip(Player player, ArmorTier tier) {
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.DARKNESS);
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        if (every(20) && player.getWorld().getEnvironment() == World.Environment.NETHER && own(player)) {
            Effects.potion(player, PotionEffectType.NIGHT_VISION, 320, 0);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEffect(EntityPotionEffectEvent event) {
        PotionEffect added = event.getNewEffect();
        if (added == null || !(event.getEntity() instanceof Player player)) return;
        PotionEffectType type = added.getType();
        if ((type == PotionEffectType.BLINDNESS || type == PotionEffectType.DARKNESS) && worn(player)) event.setCancelled(true);
    }
}
