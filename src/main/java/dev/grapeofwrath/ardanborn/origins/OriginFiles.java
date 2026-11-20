package dev.grapeofwrath.ardanborn.origins;

import com.starshootercity.OriginsAddon;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/// Copies the origin JSONs bundled in the jar into `plugins/ArdanBorn/origins/`.
///
/// Only files that are missing are copied; existing files are never overwritten, so server owners
/// can edit our origins (e.g. `"unchoosable": "true"` to hide one) and new origins added in later
/// versions still appear on update. A deleted file is restored on the next start.
public final class OriginFiles {
    private static final String DIR = "origins/";

    private OriginFiles() {}

    public static void syncBundled(OriginsAddon plugin) {
        int copied = 0;
        try (JarFile jar = new JarFile(plugin.getFile())) {
            var entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (entry.isDirectory() || !name.startsWith(DIR) || !name.endsWith(".json")) continue;
                if (new File(plugin.getDataFolder(), name).exists()) continue;
                plugin.saveResource(name, false);
                copied++;
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not read bundled origins: " + e.getMessage());
            return;
        }
        if (copied > 0) plugin.getLogger().info("Installed " + copied + " origin file(s) into " + DIR);
    }
}
