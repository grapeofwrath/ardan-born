package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.armor.effects.ForesightEffect;
import dev.grapeofwrath.ardanborn.armor.effects.HouseOfElrondEffect;
import dev.grapeofwrath.ardanborn.armor.effects.RidersGraceEffect;
import dev.grapeofwrath.ardanborn.armor.effects.SoftLandingEffect;
import dev.grapeofwrath.ardanborn.armor.effects.StarWovenEffect;
import dev.grapeofwrath.ardanborn.weapons.RaceTemplate;
import net.kyori.adventure.text.format.TextColor;

/// Rivendell: the Peredhil's armor set, made with the Evenstar.
public final class ImladrisSet extends RaceSet {
    public static final String ID = "imladris";

    public ImladrisSet() {
        super(ID, "Imladris", TextColor.color(0xC9D8FF), RaceTemplate.EVENSTAR,
                new ForesightEffect(ID), new StarWovenEffect(ID), new RidersGraceEffect(ID),
                new SoftLandingEffect(ID), new HouseOfElrondEffect(ID));
    }
}
