package dev.grapeofwrath.ardanborn.items.rings;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/// The Rings of Power. The [#id()] is stored on items, so never rename a constant once released.
public enum RingType {
    SEVEN("Ring of the Seven", TextColor.color(0xE0B84A), Material.GOLD_NUGGET, 0.5, false,
            "Gold it breeds, and the hunger for gold.",
            List.of("Ores yield as though with Fortune +2,", "and at times gold nuggets",
                    "Each ore mined feeds your greed")),
    NINE("Ring of the Nine", TextColor.color(0xA7A9B0), Material.IRON_NUGGET, 1.5, false,
            "They had long life of it, and no rest after.",
            List.of("Deathless: a killing blow leaves", "you 1 heart, and Shadow (+40)",
                    "Monsters beyond 8 blocks see you not")),
    NARYA("Narya, the Ring of Fire", TextColor.color(0xE2513A), Material.GOLD_NUGGET, 0.25, true,
            "Red is its stone, and hearts are warmed by it.",
            List.of("Fire does not harm you, and your blows", "set foes alight; you and players within",
                    "8 blocks have Strength I")),
    NENYA("Nenya, the Ring of Adamant", TextColor.color(0xE4ECF2), Material.IRON_NUGGET, 0.25, true,
            "White is its stone, and it keeps what it guards.",
            List.of("Water Breathing, Dolphin's Grace and", "Resistance I are yours, and your gear",
                    "loses no durability")),
    VILYA("Vilya, the Ring of Air", TextColor.color(0x4C7BE0), Material.GOLD_NUGGET, 0.25, true,
            "Blue is its stone, and it is chief of the Three.",
            List.of("You have Speed I and Slow Falling,", "and with players within 8 blocks",
                    "heal 1 heart every 5s")),
    ONE("The One Ring", TextColor.color(0xF5C542), Material.GOLD_NUGGET, 2.0, true,
            "It answers to one master, and not to you.",
            List.of("Strength II and Resistance I are yours,", "monsters beyond 6 blocks see you not,",
                    "and you are Deathless (+40 Shadow)"));

    private final String displayName;
    private final TextColor color;
    private final Material look;
    private final double shadowPerSecond;
    private final boolean unique;
    private final String flavor;
    private final List<String> powers;

    RingType(String displayName, TextColor color, Material look, double shadowPerSecond, boolean unique,
             String flavor, List<String> powers) {
        this.displayName = displayName;
        this.color = color;
        this.look = look;
        this.shadowPerSecond = shadowPerSecond;
        this.unique = unique;
        this.flavor = flavor;
        this.powers = powers;
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String displayName() {
        return displayName;
    }

    public TextColor color() {
        return color;
    }

    public Material look() {
        return look;
    }

    /** Shadow gained each second the ring is on (halved for Hobbits). */
    public double shadowPerSecond() {
        return shadowPerSecond;
    }

    /** Only one of this ring exists on the server at a time. */
    public boolean unique() {
        return unique;
    }

    public String flavor() {
        return flavor;
    }

    public List<String> powers() {
        return powers;
    }

    /** Deathless: Nine and One. */
    public boolean deathless() {
        return this == NINE || this == ONE;
    }

    /** Monsters farther than this ignore the bearer, or 0 for no wraith-world. */
    public double wraithRange() {
        return switch (this) {
            case NINE -> 8;
            case ONE -> 6;
            default -> 0;
        };
    }

    public static @Nullable RingType byId(@Nullable String id) {
        if (id == null) return null;
        for (RingType type : values()) {
            if (type.id().equals(id)) return type;
        }
        return null;
    }
}
