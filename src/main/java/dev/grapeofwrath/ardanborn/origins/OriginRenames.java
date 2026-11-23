package dev.grapeofwrath.ardanborn.origins;

import com.starshootercity.OriginsReborn;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

/// Moves servers from older versions onto the current origin file names and power ids, which were
/// renamed after 0.10.0 to match the names players see. Must run before [OriginFiles#syncBundled], or the
/// old and new files of an origin would both load. Does nothing once everything is renamed.
///
/// Players keep their origins: Origins-Reborn saves the display name, which didn't change.
public final class OriginRenames {
    private static final Map<String, String> FILES = Map.ofEntries(
            Map.entry("alchemist", "herbalist"),
            Map.entry("bard", "minstrel"),
            Map.entry("champion", "defender"),
            Map.entry("cleric", "healer"),
            Map.entry("druid", "beastfriend"),
            Map.entry("eagle", "great_eagle"),
            Map.entry("fighter", "warrior"),
            Map.entry("inventor", "smith"),
            Map.entry("rogue", "burglar"),
            Map.entry("size_normal", "normal_size"),
            Map.entry("size_small", "small_size"),
            Map.entry("wizard", "loremaster"));

    private static final Map<String, String> POWERS = Map.ofEntries(
            Map.entry("bloodlust", "battle_fury"),
            Map.entry("eldar_body", "undying_body"),
            Map.entry("eldar_lore", "deep_lore"),
            Map.entry("falmari_lungs", "breath_of_the_sea"),
            Map.entry("gentle_friend", "friend_of_beasts"),
            Map.entry("keen_tracker", "hunt_prey"),
            Map.entry("kings_hands", "hands_of_the_healer"),
            Map.entry("last_stand", "hope_unquenched"),
            Map.entry("master_smith", "mending_craft"),
            Map.entry("tree_roots", "tough_as_old_tree_roots"),
            Map.entry("wizard_staff", "light_of_the_staff"));

    private OriginRenames() {}

    public static void apply(Path dataFolder, Logger log) {
        Path dir = dataFolder.resolve("origins");
        if (Files.isDirectory(dir)) {
            renameFiles(dir, log);
            renamePowers(dir, log);
        }
        renameDefaults(log);
    }

    /// Server owners' edits travel with the file. If the new file is already there, the old one is set
    /// aside as `.json.old`, which Origins-Reborn doesn't load.
    private static void renameFiles(Path dir, Logger log) {
        int renamed = 0;
        for (var entry : FILES.entrySet()) {
            Path from = dir.resolve(entry.getKey() + ".json");
            Path to = dir.resolve(entry.getValue() + ".json");
            if (!Files.exists(from)) continue;
            try {
                Files.move(from, Files.exists(to) ? unusedBackup(from) : to);
                renamed++;
            } catch (IOException e) {
                log.severe("Could not rename " + from + ": " + e);
            }
        }
        if (renamed > 0) log.info("Renamed " + renamed + " origin file(s) to match their origins' names");
    }

    private static Path unusedBackup(Path file) {
        Path backup = file.resolveSibling(file.getFileName() + ".old");
        for (int i = 2; Files.exists(backup); i++) backup = file.resolveSibling(file.getFileName() + ".old" + i);
        return backup;
    }

    private static void renamePowers(Path dir, Logger log) {
        int updated = 0;
        try (Stream<Path> files = Files.list(dir)) {
            for (Path file : files.filter(f -> f.toString().endsWith(".json")).toList()) {
                String before = Files.readString(file);
                String after = before;
                for (var entry : POWERS.entrySet()) {
                    after = after.replace("\"ardanborn:" + entry.getKey() + "\"", "\"ardanborn:" + entry.getValue() + "\"");
                }
                if (after.equals(before)) continue;
                Files.writeString(file, after);
                updated++;
            }
        } catch (IOException e) {
            log.severe("Could not update power ids in " + dir + ": " + e);
        }
        if (updated > 0) log.info("Updated renamed power ids in " + updated + " origin file(s)");
    }

    /// Origins-Reborn's default origin for a layer is a file name.
    private static void renameDefaults(Logger log) {
        OriginsReborn origins = OriginsReborn.getInstance();
        FileConfiguration config = origins.getConfig();
        ConfigurationSection defaults = config.getConfigurationSection("origin-selection.default-origin");
        if (defaults == null) return;
        boolean changed = false;
        for (String layer : defaults.getKeys(false)) {
            String renamed = FILES.get(defaults.getString(layer, ""));
            if (renamed == null) continue;
            defaults.set(layer, renamed);
            changed = true;
        }
        if (changed) {
            origins.saveConfig();
            log.info("Updated renamed default origins in Origins-Reborn's config.yml");
        }
    }
}
