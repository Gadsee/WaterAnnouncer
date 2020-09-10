package me.gadse.waterannouncer.messages;

import com.google.gson.*;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

    private static final JsonParser JSON_PARSER = new JsonParser();
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");

    public static boolean isValidJson(String json) {
        try {
            JsonElement jsonElement = JSON_PARSER.parse(json);
            return jsonElement instanceof JsonObject || jsonElement instanceof JsonArray;
        } catch (JsonSyntaxException ignored) {
        }

        return false;
    }

    public static String color(String text) {
        if (text == null || text.isEmpty())
            return "";

        Matcher rgbMatcher = HEX_PATTERN.matcher(text);

        if (!rgbMatcher.lookingAt())
            return ChatColor.translateAlternateColorCodes('&', text);

        StringBuffer rgbBuilder = new StringBuffer();
        rgbMatcher.reset();

        while (rgbMatcher.find()) {
            String tempHex = rgbMatcher.group(1);
            StringBuilder hex = new StringBuilder();
            for (char c : tempHex.toCharArray())
                hex.append("&").append(c);

            rgbMatcher.appendReplacement(rgbBuilder, "&x" + hex.toString());
        }
        rgbMatcher.appendTail(rgbBuilder);

        return ChatColor.translateAlternateColorCodes('&', rgbBuilder.toString());
    }
}
