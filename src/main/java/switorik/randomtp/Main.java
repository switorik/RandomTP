package switorik.randomtp;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class Main extends JavaPlugin {

    public static Main plugin;
    public static YamlConfiguration message;

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;
        getServer().getLogger().info("Random teleporter has initiated.");
        this.getCommand("wild").setExecutor(new wildcmd());
        saveResource("config.yml", false);
        saveResource("messages.yml", false);
        Objects.requireNonNull(this.getCommand("wild")).setTabCompleter(new tabComplete());

        setupMessages();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getLogger().info("Random teleporter has stopped.");
    }

    public static void setupMessages() {

        File file = new File(plugin.getDataFolder() + File.separator + "messages.yml");
        message = YamlConfiguration.loadConfiguration(file);

    }

}

