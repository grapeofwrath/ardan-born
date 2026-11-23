package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.armor.effects.HelcaraxeEffect;
import dev.grapeofwrath.ardanborn.armor.effects.LoreWiseEffect;
import dev.grapeofwrath.ardanborn.armor.effects.SpellWardEffect;
import dev.grapeofwrath.ardanborn.armor.effects.UncloudedEffect;
import dev.grapeofwrath.ardanborn.armor.effects.WrathOfFingolfinEffect;
import dev.grapeofwrath.ardanborn.weapons.RaceTemplate;
import net.kyori.adventure.text.format.TextColor;

/// The Hidden City: the Noldor's armor set, made with the Light of the Trees.
public final class GondolinSet extends RaceSet {
    public static final String ID = "gondolin";

    public GondolinSet() {
        super(ID, "Gondolin", TextColor.color(0xBFD3F2), RaceTemplate.LIGHT_OF_THE_TREES,
                new UncloudedEffect(ID), new SpellWardEffect(ID), new LoreWiseEffect(ID),
                new HelcaraxeEffect(ID), new WrathOfFingolfinEffect(ID));
    }
}
