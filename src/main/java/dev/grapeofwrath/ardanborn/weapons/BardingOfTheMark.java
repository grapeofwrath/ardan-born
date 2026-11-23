package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.mounts.Riding;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import java.util.List;

/// The Barding of the Mark: horse armor that softens the harm done to the horse. Under one of the Rohirrim
/// the horse slowly heals.
public final class BardingOfTheMark extends RaceItem implements Listener {
    private static final long HEAL_TICKS = 100;

    public BardingOfTheMark() {
        super("barding_of_the_mark", "Barding of the Mark", Template.of(RaceTemplate.BANNER_OF_THE_MARK),
                OriginCheck.ROHIRRIM, List.of(Material.DIAMOND_HORSE_ARMOR, Material.NETHERITE_HORSE_ARMOR));
    }

    @Override
    protected List<String> flavor() {
        return List.of("The horses of the Mark are dear as kin.");
    }

    @Override
    protected List<String> lines() {
        return List.of("The horse that wears it is hardier");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Rohirrim: and heals as you ride");
    }

    public void start(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                AbstractHorse mount = Riding.mount(player);
                if (mount != null && wears(mount) && own(player)) Effects.heal(mount, ItemPower.amount(id, 2));
            }
        }, HEAL_TICKS, HEAL_TICKS);
    }

    private boolean wears(AbstractHorse horse) {
        return ItemPower.enabled(id) && horse.getEquipment() != null
                && is(horse.getEquipment().getItem(EquipmentSlot.BODY));
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (Effects.UNSTOPPABLE.contains(event.getCause())) return;
        if (event.getEntity() instanceof AbstractHorse horse && wears(horse)) {
            event.setDamage(event.getDamage() * ItemPower.reduction(id, 0.2));
        }
    }
}
