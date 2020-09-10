package me.gadse.waterannouncer.messages;

import java.util.regex.Pattern;

public enum PlaceholderType {
    COMMAND,
    DESCRIPTION,
    TIME,
    ANNOUNCEMENT_NAME,
    ANNOUNCEMENT_INTERVAL,
    ANNOUNCEMENT_SERVER;

    private final Pattern pattern = Pattern.compile("%" + name().toLowerCase() + "%");

    public Placeholder with(String replacement) {
        return new Placeholder(this, replacement);
    }

    public String apply(String text, String replacement) {
        return pattern.matcher(text).replaceAll(replacement);
    }
}
