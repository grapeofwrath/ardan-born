package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.entity.Player;

import java.util.List;

/// Full set: you and those beside you slowly heal.
public final class HouseOfElrondEffect extends PieceEffect {
    public HouseOfElrondEffect(String setId) {
        super("house_of_elrond", "House of Elrond", setId, null, OriginCheck.PEREDHIL);
    }

    @Override
    protected List<String> lines() {
        return List.of("You and players near you slowly heal", "Peredhil: faster");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        if (!every(own(player) ? 120 : 160)) return;
        for (Player each : Effects.company(player, 8)) Effects.heal(each, ItemPower.amount(id, 2));
    }
}
