package eu.manuelgu.versionanalyze;

import com.google.common.io.ByteStreams;
import eu.manuelgu.versionanalyze.commands.PlayerVersionCommand;
import eu.manuelgu.versionanalyze.database.Redis;
import eu.manuelgu.versionanalyze.listeners.EventListener;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VersionAnalyzePlugin extends Plugin {
    private static VersionAnalyzePlugin instance;

    /**
     * The Redis instance
     */
    @Getter
    private Redis redis;

    /**
     * The configuration for this plugin
     */
    @Getter
    private Configuration config;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize configuration
        try {
            loadConfig();
        } catch (IOException e) {
            getLogger().warning("Error while loading config - " + e.getMessage());
        }

        // Register listeners
        getProxy().getPluginManager().registerListener(this, new EventListener(this));

        // Register commands
        getProxy().getPluginManager().registerCommand(this, new PlayerVersionCommand(this));

        // Initialize Redis db
        redis = initRedis();
        redis.initialize();
    }

    /**
     * Initialize Redis by parsing config values for connection credentials and build {@link Redis}
     * object.
     *
     * @return Fully-qualified {@link Redis} object
     */
    private Redis initRedis() {
        String host = getConfig().getString("redis.host");
        int port = getConfig().getInt("redis.port");
        int timeout = getConfig().getInt("redis.timeout");
        String auth = getConfig().getString("redis.auth");

        return new Redis(host, port, timeout, auth);

    }

    /**
     * Load configuration in data folder for this plugin.
     * If the data folder is not present, create it first.
     */
    private void loadConfig() throws IOException {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }

        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
    }
}
