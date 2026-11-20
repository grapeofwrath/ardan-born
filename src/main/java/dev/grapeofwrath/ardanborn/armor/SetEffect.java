package dev.grapeofwrath.ardanborn.armor;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

/// An effect granted by wearing a set piece (or the full set). It can be stronger on the netherite variant.
///
/// Effects that also implement [org.bukkit.event.Listener] are registered automatically.
public interface SetEffect {

    /// Short name shown in item lore, e.g. "Snowtread".
    String name();

    /// Lore lines explaining what the effect does at the given tier.
    List<Component> describe(ArmorTier tier);

    /// Called when the player starts wearing it, or when its tier changes.
    default void onEquip(Player player, ArmorTier tier) {}

    /// Called when the player stops wearing it, quits, or the plugin disables.
    default void onUnequip(Player player) {}

    /// Called every [ArmorEffectManager#TICK_PERIOD] ticks while worn.
    default void tick(Player player, ArmorTier tier) {}
}
