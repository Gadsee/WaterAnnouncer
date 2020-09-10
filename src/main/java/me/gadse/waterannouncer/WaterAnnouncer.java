package me.gadse.waterannouncer;

import me.gadse.waterannouncer.messages.Messages;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public final class WaterAnnouncer extends Plugin {

    private File configFile;
    private Configuration config;

    private final Map<String, Announcement> announcements = new HashMap<>();

    @Override
    public void onEnable() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs())
            throw new IllegalStateException("Data folder inaccessible.");

        configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            InputStream in = getResourceAsStream("config.yml");
            try {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        reload();

        getProxy().getPluginManager().registerCommand(this, new WaterAnnouncerCommand(this));
    }

    public void reload() {
        announcements.values().forEach(Announcement::stop);
        announcements.clear();

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Messages message : Messages.values())
            message.updateMessage(this);

        config.getKeys().forEach(key -> {
            if (key.equals("messages"))
                return;

            try {
                announcements.put(key, new Announcement(this, key));
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void onDisable() {
        announcements.values().forEach(Announcement::stop);
        announcements.clear();
    }

    public Configuration getConfig() {
        return config;
    }

    public Map<String, Announcement> getAnnouncements() {
        return announcements;
    }
}
