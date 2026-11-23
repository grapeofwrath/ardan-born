package dev.grapeofwrath.ardanborn.items;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/// How strong race items are, from `race-items` in config.yml: one power level for all of them, a level of
/// its own for any item or effect, and a list of ids that do nothing.
///
/// Bonus damage, damage reduction, damage dealt, healing and chances scale. Cooldowns, ranges, speed and
/// durations don't. At power 1 every number is as written in the code.
public final class ItemPower {
    private static final double MIN = 0.5;
    private static final double MAX = 3.0;
    private static final double MAX_REDUCTION = 0.5;
    private static final double MAX_CHANCE = 0.6;

    private static double power = 1.0;
    private static final Map<String, Double> OVERRIDES = new HashMap<>();
    private static final Set<String> DISABLED = new HashSet<>();

    private ItemPower() {}

    public static void load(ConfigurationSection config) {
        power = clamp(config.getDouble("race-items.power", 1.0));
        OVERRIDES.clear();
        ConfigurationSection overrides = config.getConfigurationSection("race-items.overrides");
        if (overrides != null) {
            for (String id : overrides.getKeys(false)) OVERRIDES.put(id, clamp(overrides.getDouble(id, power)));
        }
        DISABLED.clear();
        DISABLED.addAll(config.getStringList("race-items.disabled"));
    }

    /// Warns about ids in the config that match nothing, which are usually typos.
    public static void warnUnknown(Set<String> known, Logger log) {
        Set<String> unknown = new TreeSet<>(OVERRIDES.keySet());
        unknown.addAll(DISABLED);
        unknown.removeAll(known);
        if (!unknown.isEmpty()) log.warning("Unknown ids under race-items in config.yml: " + String.join(", ", unknown));
    }

    private static double clamp(double value) {
        return Math.clamp(value, MIN, MAX);
    }

    private static double of(String id) {
        return OVERRIDES.getOrDefault(id, power);
    }

    public static boolean enabled(String id) {
        return !DISABLED.contains(id);
    }

    /// Damage multiplier for a bonus, e.g. 0.25 for +25%.
    public static double bonus(String id, double bonus) {
        return 1 + bonus * of(id);
    }

    /// Damage multiplier for a reduction, e.g. 0.12 for 12% less. Never more than half.
    public static double reduction(String id, double reduction) {
        return 1 - Math.min(MAX_REDUCTION, reduction * of(id));
    }

    /// An amount of damage or healing.
    public static double amount(String id, double amount) {
        return amount * of(id);
    }

    public static double chance(String id, double chance) {
        return Math.min(MAX_CHANCE, chance * of(id));
    }

    public static boolean roll(String id, double chance) {
        return ThreadLocalRandom.current().nextDouble() < chance(id, chance);
    }
}
