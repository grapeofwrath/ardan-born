package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.items.Effects;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/// Orc-draught: burning liquor that drives the body on, at a price Uruk-hai don't pay.
public final class OrcDraught extends Artifact {
    public OrcDraught() {
        super("orc_draught", "Orc-draught", Template.of(RaceTemplate.WHITE_HAND),
                OriginCheck.URUK_HAI, Material.GLASS_BOTTLE);
    }

    private static final NamespacedKey KEY = Keys.key("orc_draught");
    private static final double COST = 4; // 2 hearts
    private static final int TICKS = 200;

    @Override
    protected List<String> lines() {
        return List.of("Right-click: trade health for Speed", "and faster attacks");
    }

    @Override
    protected List<String> ownLines() {
        return List.of("Uruk-hai: costs no health");
    }

    @Override
    protected long cooldownMs(Player player) {
        return 60_000;
    }

    @Override
    protected boolean ready(Player player) {
        if (own(player) || player.getHealth() > COST) return true;
        player.sendActionBar(Component.text("You are too weak for it", NamedTextColor.GRAY));
        return false;
    }

    @Override
    protected void use(Player player) {
        if (!own(player)) player.setHealth(player.getHealth() - COST);
        Effects.potion(player, PotionEffectType.SPEED, TICKS, 0);
        Effects.scale(player, Attribute.ATTACK_SPEED, KEY, 0.2);
        Bukkit.getScheduler().runTaskLater(Effects.plugin(),
                () -> Effects.scale(player, Attribute.ATTACK_SPEED, KEY, 0), TICKS);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1f, 0.7f);
    }
}
