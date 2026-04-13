package me.lukavns.commandframework.api.exception;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class ExceptionMessageHolder {

    private final Map<MessageType, String> messages = new EnumMap<MessageType, String>(MessageType.class);

    public ExceptionMessageHolder() {
        for (MessageType type : MessageType.values()) {
            this.messages.put(type, type.defaultMessage());
        }
    }

    public String getMessage(MessageType type) {
        return this.messages.get(Objects.requireNonNull(type, "type"));
    }

    public ExceptionMessageHolder setMessage(MessageType type, String message) {
        this.messages.put(Objects.requireNonNull(type, "type"), Objects.requireNonNull(message, "message"));
        return this;
    }

    public Map<MessageType, String> asMap() {
        return Collections.unmodifiableMap(this.messages);
    }

    public String render(MessageType type, Map<String, String> placeholders) {
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
