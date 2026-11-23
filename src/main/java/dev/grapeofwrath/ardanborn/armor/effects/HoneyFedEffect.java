package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/// Helmet: honey heals you.
public final class HoneyFedEffect extends PieceEffect implements Listener {
    private final OriginAbilities.Cooldown cooldown = new OriginAbilities.Cooldown();

    public HoneyFedEffect(String setId) {
        super("honey_fed", "Honey-fed", setId, EquipmentSlot.HEAD, OriginCheck.BEORNING);
    }

    @Override
    protected List<String> lines() {
        return List.of("Honey bottles heal you", "Beornings: and heal you more");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrink(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType() != Material.HONEY_BOTTLE || !worn(player)) return;
        if (!cooldown.tryUseQuiet(player, LoreWiseEffect.shorten(player, 10_000))) return;
        Effects.heal(player, ItemPower.amount(id, own(player) ? 6 : 4));
    }
}
