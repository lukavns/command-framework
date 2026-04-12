package me.lukavns.commandframework.api.exception;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class CommandMessageHolder {

    private final Map<CommandMessageType, String> messages = new EnumMap<CommandMessageType, String>(CommandMessageType.class);

    public CommandMessageHolder() {
        for (CommandMessageType type : CommandMessageType.values()) {
            this.messages.put(type, type.defaultMessage());
        }
    }

    public String getMessage(CommandMessageType type) {
        return this.messages.get(Objects.requireNonNull(type, "type"));
    }

    public CommandMessageHolder setMessage(CommandMessageType type, String message) {
        this.messages.put(Objects.requireNonNull(type, "type"), Objects.requireNonNull(message, "message"));
        return this;
    }

    public Map<CommandMessageType, String> asMap() {
        return Collections.unmodifiableMap(this.messages);
    }

    public String render(CommandMessageType type, Map<String, String> placeholders) {
        String rendered = getMessage(type);
        if (rendered == null || rendered.isEmpty() || placeholders == null || placeholders.isEmpty()) {
            return rendered;
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String value = entry.getValue();
            rendered = rendered.replace("{" + entry.getKey() + "}", value == null ? "" : value);
        }
        return rendered;
    }
}
