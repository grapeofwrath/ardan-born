package dev.grapeofwrath.ardanborn.origins;

import com.starshootercity.AddonLoader;
import com.starshootercity.Origin;
import com.starshootercity.OriginSwapper;
import org.bukkit.entity.Player;

/// Which origin a player has, for the extras race items give their own people (e.g. the Teleri's bonus
/// with the Spear of the Havens).
///
/// Origins are matched by file name (`teleri` for `origins/teleri.json`), so renaming an origin's
/// display name doesn't break anything.
public final class OriginCheck {
    public static final String TELERI = "teleri";
    public static final String DWARF = "dwarf";
    public static final String GREAT_EAGLE = "great_eagle";
    public static final String BEORNING = "beorning";
    public static final String NOLDOR = "noldor";
    public static final String SILVAN = "silvan";
    public static final String DUNEDAIN = "dunedain";
    public static final String PEREDHIL = "peredhil";
    public static final String HOBBIT = "hobbit";
    public static final String URUK_HAI = "uruk_hai";
    public static final String ROHIRRIM = "rohirrim";

    private OriginCheck() {}

    public static boolean is(Player player, String originFile) {
        // Origins-Reborn keeps file names with spaces for underscores.
        Origin origin = AddonLoader.getOriginByFilename(originFile.replace('_', ' '));
        return origin != null && OriginSwapper.getOrigins(player).contains(origin);
    }
}
