package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.mounts.Riding;
import dev.grapeofwrath.ardanborn.mounts.RohirrimDamage;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/// The Spear of the Mark: it strikes harder from the saddle, and more so for the Rohirrim.
public final class SpearOfTheMark extends RaceItem {
    public SpearOfTheMark() {
        super("spear_of_the_mark", "Spear of the Mark", Template.of(RaceTemplate.BANNER_OF_THE_MARK),
                OriginCheck.ROHIRRIM, List.of(Material.DIAMOND_SPEAR, Material.NETHERITE_SPEAR));
        RohirrimDamage.add(this::bonus);
    }

    @Override
    protected List<String> lines() {
        return List.of("From the saddle it strikes harder");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Rohirrim: harder still");
    }

    private double bonus(Player player) {
        if (!Riding.mounted(player) || !wields(player)) return 0;
        return ItemPower.bonus(id, own(player) ? 0.25 : 0.15) - 1;
    }
}
