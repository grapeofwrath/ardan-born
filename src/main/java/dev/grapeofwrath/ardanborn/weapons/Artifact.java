package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.armor.effects.LoreWiseEffect;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/// A race item used by right-clicking with it in the main hand. It doesn't stack, and whatever its base
/// item does when used is stopped.
public abstract class Artifact extends RaceItem implements Listener {
    private final OriginAbilities.Cooldown cooldown = new OriginAbilities.Cooldown();

    protected Artifact(String id, String name, Template template, String people, Material base) {
        super(id, name, template, people, List.of(base));
    }

    protected abstract long cooldownMs(Player player);

    /// Whether it can be used right now. Tell the player if not.
    protected boolean ready(Player player) {
        return true;
    }

    protected abstract void use(Player player);

    @Override
    public ItemStack upgrade(ItemStack base) {
        ItemStack item = super.upgrade(base);
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return item;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!is(event.getItem())) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND || !ItemPower.enabled(id)) return;
        if (!ready(player)) return;
        if (cooldown.tryUse(player, LoreWiseEffect.shorten(player, cooldownMs(player)))) use(player);
    }

    /// A lead would be tied to the animal.
    @EventHandler(priority = EventPriority.LOW)
    public void onUseOnEntity(PlayerInteractEntityEvent event) {
        if (is(event.getPlayer().getInventory().getItem(event.getHand()))) event.setCancelled(true);
    }
}
