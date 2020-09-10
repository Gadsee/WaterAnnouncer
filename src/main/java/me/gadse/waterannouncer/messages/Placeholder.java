package me.gadse.waterannouncer.messages;

public class Placeholder {
    private final PlaceholderType placeholderType;
    private final String replacement;

    public Placeholder(PlaceholderType placeholderType, String replacement) {
        this.placeholderType = placeholderType;
        this.replacement = replacement;
    }

    public String apply(String text) {
        return placeholderType.apply(text, replacement);
    }
}
