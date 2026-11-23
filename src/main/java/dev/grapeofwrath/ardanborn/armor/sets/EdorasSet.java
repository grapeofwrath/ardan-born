package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.armor.effects.DeathEffect;
import dev.grapeofwrath.ardanborn.armor.effects.FirmInTheSaddleEffect;
import dev.grapeofwrath.ardanborn.armor.effects.HorsetailCrestEffect;
import dev.grapeofwrath.ardanborn.armor.effects.TramplingEffect;
import dev.grapeofwrath.ardanborn.armor.effects.WindInTheGrassEffect;
import dev.grapeofwrath.ardanborn.weapons.RaceTemplate;
import net.kyori.adventure.text.format.TextColor;

/// The riders of the King's house: the Rohirrim's armor set, made with the Banner of the Mark.
public final class EdorasSet extends RaceSet {
    public static final String ID = "edoras";

    public EdorasSet() {
        super(ID, "Edoras", TextColor.color(0x5FAE5B), RaceTemplate.BANNER_OF_THE_MARK,
                new HorsetailCrestEffect(ID), new WindInTheGrassEffect(ID), new FirmInTheSaddleEffect(ID),
                new TramplingEffect(ID), new DeathEffect(ID));
    }
}
