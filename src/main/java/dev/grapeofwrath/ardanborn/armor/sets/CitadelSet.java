package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.armor.effects.LastAllianceEffect;
import dev.grapeofwrath.ardanborn.armor.effects.NumenoreanEffect;
import dev.grapeofwrath.ardanborn.armor.effects.SteadfastEffect;
import dev.grapeofwrath.ardanborn.armor.effects.StridersGaitEffect;
import dev.grapeofwrath.ardanborn.armor.effects.WhiteTreeEffect;
import dev.grapeofwrath.ardanborn.weapons.RaceTemplate;
import net.kyori.adventure.text.format.TextColor;

/// The Guards of the Citadel: the Edain's armor set, made with the Star of Elendil.
public final class CitadelSet extends RaceSet {
    public static final String ID = "citadel";

    public CitadelSet() {
        super(ID, "Citadel", TextColor.color(0xDCE6F5), RaceTemplate.STAR_OF_ELENDIL,
                new SteadfastEffect(ID), new WhiteTreeEffect(ID), new StridersGaitEffect(ID),
                new NumenoreanEffect(ID), new LastAllianceEffect(ID));
    }
}
