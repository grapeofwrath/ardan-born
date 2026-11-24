package dev.grapeofwrath.ardanborn.adornments;

import com.starshootercity.version.ResourcePackInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Set;
import java.util.UUID;

/// The optional resource pack of the adornments screen. Origins-Reborn sends it along with its own.
///
/// The zip is published with the docs (docs/static/pack) and also lies in the jar, only so that its hash can be
/// worked out. Never change a published zip: give it a new name, since older versions still ask for it.
public final class AdornmentPack implements Listener {
    private static final String FILE = "adornments-1.zip";
    private static final String URL = "https://grapeofwrath.github.io/ardan-born/pack/" + FILE;
    private static final UUID ID = UUID.fromString("5d0c7a52-3f0e-4e0b-9a6c-2b8f4a1d6e01");
    private static final Set<UUID> LOADED = new HashSet<>();

    /** Whether the player's client has loaded the pack. */
    static boolean loaded(Player player) {
        return LOADED.contains(player.getUniqueId());
    }

    /** What Origins-Reborn is to send, or null if the config turns it off. */
    public static @Nullable ResourcePackInfo info(JavaPlugin plugin) {
        if (!plugin.getConfig().getBoolean("adornments.resource-pack", true)) return null;
        // Only for playtests, before the pack is published.
        String url = System.getProperty("ardanborn.pack-url", URL);
        try (InputStream in = plugin.getResource("pack/" + FILE)) {
            String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(in.readAllBytes()));
            return new ResourcePackInfo(net.kyori.adventure.resource.ResourcePackInfo.resourcePackInfo(ID, URI.create(url), hash));
        } catch (Exception e) {
            plugin.getLogger().warning("No resource pack for the adornments screen: " + e);
            return null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStatus(PlayerResourcePackStatusEvent event) {
        if (!ID.equals(event.getID())) return;
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) LOADED.add(event.getPlayer().getUniqueId());
        else LOADED.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        LOADED.remove(event.getPlayer().getUniqueId());
    }
}
