package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.List;

/// Full set: the closer to death, the faster the blows.
public final class BerserkerEffect extends PieceEffect {
    private static final NamespacedKey KEY = Keys.key("berserker");

    public BerserkerEffect(String setId) {
        super("berserker", "Berserker", setId, null, OriginCheck.URUK_HAI);
    }

    @Override
    protected List<String> lines() {
        return List.of("Attack faster when badly hurt", "Uruk-hai: move faster too");
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        boolean low = !player.isDead() && player.getHealth() < Effects.maxHealth(player) / 2;
        Effects.scale(player, Attribute.ATTACK_SPEED, KEY, low ? 0.2 : 0);
        Effects.scale(player, Attribute.MOVEMENT_SPEED, KEY, low && own(player) ? 0.1 : 0);
    }

    @Override
    public void onUnequip(Player player) {
        Effects.scale(player, Attribute.ATTACK_SPEED, KEY, 0);
        Effects.scale(player, Attribute.MOVEMENT_SPEED, KEY, 0);
    }
}
