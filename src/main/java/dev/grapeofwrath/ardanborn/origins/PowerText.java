package dev.grapeofwrath.ardanborn.origins;

import com.starshootercity.OriginsReborn;
import com.starshootercity.Translator;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/// Rewords powers from Origins-Reborn and its addons that our origins use, so they read well in
/// Middle-earth without sounding wrong on the stock origins that share them (the text is per power,
/// not per origin).
///
/// Origins-Reborn keeps every power's text in its `translations.yml`. An entry is only replaced while
/// it is missing, still the stock text, or our own earlier wording, so anything a server owner has written is left alone.
public final class PowerText {
    private record Text(String key, String field, List<String> replaceable, String ours) {}

    private static final List<Text> TEXTS = List.of(
            title("magicorigins:silent", "Spectral", "Soft-footed"),
            desc("magicorigins:silent",
                    "Your ghost-like nature prevents you being detected by Sculk Sensors when you move or eat.",
                    "You move so softly that Sculk Sensors never notice you walking or eating.",
                    "So softly do you go that no Sculk Sensor marks you walking or eating."),
            title("origins:extra_reach", "Slender Body", "Long Reach"),
            desc("origins:extra_reach",
                    "You can reach blocks and entities further away.",
                    "You can reach blocks and entities further away than others can.",
                    "Long is your arm, and you may lay hand upon blocks and creatures that lie beyond the reach of others."),
            title("fantasyorigins:double_health", "Double Health", "Mighty Frame"),
            desc("fantasyorigins:double_health",
                    "As you're larger than humans, you have more health as your body protects you from damage.",
                    "Your great frame gives you twice the health of others.",
                    "Great is your frame, and you have twice the health of others."),
            title("magicorigins:kill_boost", "Dark Magic", "Spoils of Victory"),
            desc("magicorigins:kill_boost",
                    "Whenever you kill something, you absorb some of its health for up to 30 extra hearts.",
                    "Whenever you kill something, you take in some of its vitality as extra health, up to 30 extra hearts.",
                    "From all that you slay some vigor passes into you as extra health, up to 30 extra hearts."),
            title("moborigins:queen_bee", "Queen Bee", "Bee-friend"),
            desc("moborigins:queen_bee",
                    "When you collect honey, the bees won't try to attack you.",
                    "Bees won't turn on you when you collect honey.",
                    "When you gather honey the bees do not turn upon you."),
            desc("origins:elytra",
                    "You have Elytra wings without needing to equip any.",
                    "You have wings of your own and can glide without equipping an Elytra.",
                    "Wings are your own, and you may glide without an Elytra."),
            title("moborigins:rideable_creature", "Rideable Creature", "Bearer"),
            desc("moborigins:rideable_creature",
                    "Other players can ride you!",
                    "Other players can ride on your back.",
                    "Other players may ride upon your back."),
            desc("fantasyorigins:strong_skin",
                    "You've got naturally thicker skin than regular humans, so arrows do a lot less damage.",
                    "Your hide is tough enough that arrows do much less damage to you.",
                    "Tough is your hide, and arrows deal you much less damage."),
            title("monsterorigins:infinite_arrows", "Infinite Arrows", "Endless Quiver"),
            desc("monsterorigins:infinite_arrows",
                    "Arrows you shoot are not used up.",
                    "Arrows you shoot are never used up.",
                    "The arrows that you shoot are never spent."),
            desc("fantasyorigins:small_body",
                    "You're only one block tall, half as tall as a regular human.",
                    "You're only one block tall, half the height of most folk.",
                    "You stand but one block tall, which is half the height of most folk."));

    private PowerText() {}

    private static Text title(String key, String stock, String ours) {
        return new Text(key, "title", List.of(stock), ours);
    }

    /// `previous` is the wording we shipped before 0.12.0, so servers that have it get the new one.
    private static Text desc(String key, String stock, String previous, String ours) {
        return new Text(key, "description", List.of(stock, previous), ours);
    }

    public static void apply(Logger log) {
        File file = new File(OriginsReborn.getInstance().getDataFolder(), "translations.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        int changed = 0;
        for (Text text : TEXTS) {
            String path = "translations.abilities." + text.key() + "." + text.field();
            String current = config.getString(path);
            if (current != null && !text.replaceable().contains(current)) continue;
            config.set(path, text.ours());
            changed++;
        }
        if (changed == 0) return;
        try {
            config.save(file);
        } catch (IOException e) {
            log.severe("Could not update Origins-Reborn's translations.yml: " + e.getMessage());
            return;
        }
        // Origins-Reborn holds the file in memory and re-saves it whenever an addon registers a power,
        // so reload it now or our changes would be overwritten.
        Translator.reloadTranslations();
        log.info("Reworded " + changed + " power text(s) in Origins-Reborn's translations.yml");
    }
}
