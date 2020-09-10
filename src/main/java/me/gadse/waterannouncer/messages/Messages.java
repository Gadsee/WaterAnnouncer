package me.gadse.waterannouncer.messages;

import me.gadse.waterannouncer.WaterAnnouncer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public enum Messages {
    PREFIX,
    HELP(false),
    RELOAD,
    ANNOUNCEMENT_START,
    ANNOUNCEMENT_STOP,
    ANNOUNCEMENT_INFO(false);

    private final boolean usePrefix;

    private String message = "ERROR LOADING MESSAGE";

    Messages() {
        this(true);
    }
    Messages(boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    public void updateMessage(WaterAnnouncer plugin) {
        message = MessageUtil
                .color(plugin.getConfig().getString("messages." + name().toLowerCase()));
    }

    public void send(CommandSender sender, Placeholder... placeholders) {
        String tempMessage = message;

        for (Placeholder placeholder : placeholders)
            tempMessage = placeholder.apply(tempMessage);

        if (usePrefix)
            tempMessage = PREFIX.message + tempMessage;

        sender.sendMessage(TextComponent.fromLegacyText(tempMessage));
    }
}
