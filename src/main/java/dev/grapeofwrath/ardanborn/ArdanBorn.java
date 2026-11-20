package dev.grapeofwrath.ardanborn;

import org.bukkit.plugin.java.JavaPlugin;

public final class ArdanBorn extends JavaPlugin {

    @Override
    public void onEnable() {
        getComponentLogger().info("ArdanBorn enabled");
    }

    @Override
    public void onDisable() {
    }
}
