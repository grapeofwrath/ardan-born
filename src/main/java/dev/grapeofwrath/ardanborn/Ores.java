package dev.grapeofwrath.ardanborn;

import org.bukkit.Material;
import org.bukkit.Tag;

/// Which blocks count as ore, for the Ring of the Seven and the Vein rune.
public final class Ores {
    private Ores() {}

    public static boolean isOre(Material material) {
        return Tag.COAL_ORES.isTagged(material) || Tag.IRON_ORES.isTagged(material)
                || Tag.COPPER_ORES.isTagged(material) || Tag.GOLD_ORES.isTagged(material)
                || Tag.REDSTONE_ORES.isTagged(material) || Tag.LAPIS_ORES.isTagged(material)
                || Tag.DIAMOND_ORES.isTagged(material) || Tag.EMERALD_ORES.isTagged(material)
                || material == Material.NETHER_QUARTZ_ORE;
    }
}
