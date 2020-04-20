package switorik.randtp;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Main plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;
        getServer().getLogger().info("Random teleporter has initiated.");
        this.getCommand("wild").setExecutor(new wildcmd());
        saveResource("config.yml", false);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getLogger().info("Random teleporter has stopped.");
    }
}
