package dev.grapeofwrath.ardanborn;

import com.starshootercity.OriginsAddon;
import com.starshootercity.abilities.types.Ability;
import dev.grapeofwrath.ardanborn.armor.ArmorEffectManager;
import dev.grapeofwrath.ardanborn.armor.ArmorSets;
import dev.grapeofwrath.ardanborn.armor.ArmorSmithingListener;
import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.RecipeGuardListener;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginFiles;
import dev.grapeofwrath.ardanborn.origins.OriginsDefaults;
import dev.grapeofwrath.ardanborn.origins.PowerText;
import dev.grapeofwrath.ardanborn.weapons.ToolUpgrades;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// Origins-Reborn addon (namespace "ardanborn") plus the custom armor sets.
///
/// `OriginsAddon.onEnable()` is final: it calls [#onRegister()], registers our origins and
/// abilities with Origins-Reborn, then calls [#afterRegister()].
public final class ArdanBorn extends OriginsAddon {

    private final List<Ability> abilities = OriginAbilities.all();
    private ArmorEffectManager armorEffects;

    @Override
    public @NotNull String getNamespace() {
        // Same as the plugin name, PDC keys and recipe keys; origin JSONs reference abilities as ardanborn:<id>.
        return Keys.NAMESPACE;
    }

    @Override
    public @NotNull List<Ability> getRegisteredAbilities() {
        return abilities;
    }

    @Override
    public void onRegister() {
        // Must run before Origins-Reborn reads plugins/ArdanBorn/origins/.
        OriginFiles.syncBundled(this);
        OriginsDefaults.apply(getLogger());
        PowerText.apply(getLogger());

        // Once a second, update every ability that needs periodic work.
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Ability ability : abilities) {
                    if (ability instanceof OriginAbilities.Tickable tickable) {
                        tickable.tick(player);
                    }
                }
            }
        }, 20L, 20L);

        ArmorSets.registerAll(this);
        armorEffects = new ArmorEffectManager(this);
        var pm = getServer().getPluginManager();
        pm.registerEvents(armorEffects, this);
        pm.registerEvents(new ArmorSmithingListener(), this);
        pm.registerEvents(new RecipeGuardListener(), this);
        ToolUpgrades.registerAll(this);
        armorEffects.start();
    }

    @Override
    public void onDisable() {
        if (armorEffects != null) armorEffects.shutdown();
        ArmorSets.unregisterAll();
    }
}
