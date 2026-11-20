package dev.grapeofwrath.ardanborn.origins;

import com.starshootercity.OriginsReborn;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

/// Seeds Origins-Reborn's config with sensible defaults for our extra layers, so no manual setup
/// is needed. Origins-Reborn only writes these keys when they are absent, so setting them before
/// our origins load wins on a fresh install, and anything a server owner has changed is left alone.
public final class OriginsDefaults {
    private OriginsDefaults() {}

    public static void apply(Logger log) {
        OriginsReborn origins = OriginsReborn.getInstance();
        FileConfiguration config = origins.getConfig();
        boolean changed = false;

        // Higher priority is picked first. Origins-Reborn's own "origin" layer defaults to 1.
        changed |= setIfAbsent(config, "origin-selection.layer-orders.class", 0);
        changed |= setIfAbsent(config, "origin-selection.layer-orders.size", -1);
        // Regular height unless the player picks otherwise, so the size layer never blocks joining.
        changed |= setIfAbsent(config, "origin-selection.default-origin.size", "size_normal");

        if (changed) {
            origins.saveConfig();
            log.info("Added default layer settings to Origins-Reborn's config.yml");
        }
    }

    private static boolean setIfAbsent(FileConfiguration config, String path, Object value) {
        if (config.contains(path)) return false;
        config.set(path, value);
        return true;
    }
}
