package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.armor.effects.BerserkerEffect;
import dev.grapeofwrath.ardanborn.armor.effects.DreadEffect;
import dev.grapeofwrath.ardanborn.armor.effects.ForcedMarchEffect;
import dev.grapeofwrath.ardanborn.armor.effects.FurysTreadEffect;
import dev.grapeofwrath.ardanborn.armor.effects.SpikedPlateEffect;
import dev.grapeofwrath.ardanborn.weapons.RaceTemplate;
import net.kyori.adventure.text.format.TextColor;

/// Saruman's host: the Uruk-hai's armor set, made with the White Hand.
public final class IsengardSet extends RaceSet {
    public static final String ID = "isengard";

    public IsengardSet() {
        super(ID, "Isengard", TextColor.color(0xD9D9D9), RaceTemplate.WHITE_HAND,
                new DreadEffect(ID), new SpikedPlateEffect(ID), new ForcedMarchEffect(ID),
                new FurysTreadEffect(ID), new BerserkerEffect(ID));
    }
}
