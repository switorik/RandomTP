package switorik.randomtp;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    public static Main plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;
        getServer().getLogger().info("Random teleporter has initiated.");
        this.getCommand("wild").setExecutor(new wildcmd());
        saveResource("config.yml", false);
        Objects.requireNonNull(this.getCommand("wild")).setTabCompleter(new tabComplete());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getLogger().info("Random teleporter has stopped.");
    }
}
