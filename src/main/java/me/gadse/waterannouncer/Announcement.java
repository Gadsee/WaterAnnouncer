package me.gadse.waterannouncer;

import me.gadse.waterannouncer.messages.MessageUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Announcement {

    private final WaterAnnouncer plugin;
    private final String id;
    private final List<BaseComponent[]> messages = new ArrayList<>();

    private final List<String> serverNames = new ArrayList<>();
    private final List<ServerInfo> serverInfos = new ArrayList<>();
    private int interval;
    private boolean random;
    private boolean broadcast = false;

    private ScheduledTask task;

    public Announcement(WaterAnnouncer plugin, String id) {
        this.plugin = plugin;
        this.id = id;

        reloadMessages();
    }

    public void reloadMessages() {
        stop();
        messages.clear();
        serverNames.clear();

        Configuration announcementSection = plugin.getConfig().getSection(id);
        if (announcementSection == null)
            throw new IllegalArgumentException("'" + id + "' was not a configuration section.");


        String serverName = announcementSection.getString("server");
        if (serverName == null || serverName.isEmpty())
            serverNames.addAll(announcementSection.getStringList("servers"));
        else {
            serverNames.add(serverName);
            broadcast = serverName.equalsIgnoreCase("all");
        }
        random = announcementSection.getBoolean("random", false);

        if (!broadcast) {
            serverNames.forEach(name -> {
                ServerInfo serverInfo = plugin.getProxy().getServerInfo(name);
                if (serverInfo == null) {
                    plugin.getLogger().warning("The server '" + name +
                            "' at '" + id +
                            "' does not exist.");
                    plugin.getLogger().warning("List of valid server names: " +
                            String.join(", ", plugin.getProxy().getServers().keySet()) + ".");
                    return;
                }
                serverInfos.add(serverInfo);
            });
        }

        interval = announcementSection.getInt("interval-in-seconds");

        announcementSection.getStringList("messages").forEach(message -> {
            if (MessageUtil.isValidJson(message))
                messages.add(ComponentSerializer.parse(message));
            else
                messages.add(TextComponent.fromLegacyText(MessageUtil.color(message)));
        });
        start();
    }

    public void start() {
        if (messages.size() == 0)
            return;

        if (random) {
            task = plugin.getProxy().getScheduler().schedule(plugin, () ->
                            sendMessage(messages.get(ThreadLocalRandom.current().nextInt(messages.size()))),
                    interval, interval, TimeUnit.SECONDS);
            return;
        }

        AtomicInteger counter = new AtomicInteger();
        task = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            if (counter.get() + 1 > messages.size())
                counter.set(0);

            sendMessage(messages.get(counter.get()));

            counter.set(counter.get() + 1);
        }, interval, interval, TimeUnit.SECONDS);
    }

    private void sendMessage(BaseComponent[] message) {
        if (broadcast)
            plugin.getProxy().broadcast(message);
        else
            serverInfos.forEach(serverInfo ->
                    serverInfo.getPlayers().forEach(player -> player.sendMessage(message)));
    }

    public void stop() {
        if (task != null)
            task.cancel();
    }

    public String getId() {
        return id;
    }

    public String getInterval() {
        return String.valueOf(interval);
    }

    public String getServerName() {
        return String.join(", ", serverNames);
    }
}
