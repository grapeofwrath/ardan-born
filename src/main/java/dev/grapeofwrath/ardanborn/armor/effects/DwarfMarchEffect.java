package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.Ores;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Leggings: sprinting in deep, unlit places is tireless. Naugrim work faster after each ore.
public final class DwarfMarchEffect extends PieceEffect implements Listener {
    public DwarfMarchEffect(String setId) {
        super("dwarf_march", "Dwarf-march", setId, EquipmentSlot.LEGS, OriginCheck.NAUGRIM);
    }

    @Override
    protected List<String> lines() {
        return List.of("Sprint tirelessly in the deep", "Naugrim: mining ore gives Haste");
    }

    @EventHandler(ignoreCancelled = true)
    public void onExhaust(EntityExhaustionEvent event) {
        if (event.getExhaustionReason() != EntityExhaustionEvent.ExhaustionReason.SPRINT) return;
        if (event.getEntity() instanceof Player player && worn(player) && OriginAbilities.inTheDeep(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (Ores.isOre(event.getBlock().getType()) && worn(player) && own(player)) {
            Effects.potion(player, PotionEffectType.HASTE, 100, 1);
        }
    }
}
