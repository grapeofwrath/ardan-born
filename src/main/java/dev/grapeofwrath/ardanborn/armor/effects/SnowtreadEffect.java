package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import net.kyori.adventure.text.Component;

import java.util.List;

/// Boots: walk on powder snow without sinking, and never freeze.
///
/// Vanilla grants both, on server and client alike, only to boots that are `minecraft:leather_boots`
/// (freezing immunity via the `freeze_immune_wearables` tag). So this effect needs no code: sets that use
/// it make those boots leather-based with diamond or netherite stats (see
/// [dev.grapeofwrath.ardanborn.armor.ArmorSet#baseMaterial]). This class only supplies the lore.
public final class SnowtreadEffect implements SetEffect {

    @Override
    public String name() {
        return "Snowtread";
    }

    @Override
    public List<Component> describe(ArmorTier tier) {
        return List.of(
                Component.text("Walk on powder snow without sinking"),
                Component.text("Immune to freezing"));
    }
}
