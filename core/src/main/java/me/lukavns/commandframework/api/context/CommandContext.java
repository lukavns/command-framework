package me.lukavns.commandframework.api.context;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.lukavns.commandframework.api.command.CommandDefinition;

/**
 * Represents the state available while a command is being executed or suggested.
 *
 * @param <S> sender type exposed by the current platform
 */
public interface CommandContext<S> {

    S sender();

    String label();

    List<String> rawArguments();

    boolean hasPermission(String permission);

    Optional<CommandDefinition> command();

    Map<String, Object> values();

    /**
     * Returns values injected by the framework or platform bootstrap.
     *
     * @return immutable provided value map
     */
    default Map<Class<?>, Object> providedValues() {
        return Collections.emptyMap();
    }

    /**
     * Looks up a resolved argument by parameter name and type.
     *
     * @param name logical parameter name
     * @param type expected value type
     * @param <T> typed result
     * @return matching value when present and assignable
     */
    default <T> Optional<T> value(String name, Class<T> type) {
        Object value = values().get(name);
        if (value == null || !type.isInstance(value)) {
            return Optional.empty();
        }
        return Optional.of(type.cast(value));
    }

    /**
     * Casts the current sender to the requested type or throws when incompatible.
     *
     * @param type expected sender type
     * @param <T> typed sender result
     * @return sender cast to the requested type
     */
    default <T> T senderAs(Class<T> type) {
        if (!type.isInstance(sender())) {
            throw new IllegalArgumentException("Sender is not an instance of " + type.getName());
        }
        return type.cast(sender());
    }

    /**
     * Looks up a provided value by type.
     *
     * @param type expected provided value type
     * @param <T> typed result
     * @return matching provided value when present
     */
    default <T> Optional<T> provided(Class<T> type) {
        Map<Class<?>, Object> values = providedValues();
        Object direct = values.get(type);
        if (direct != null && type.isInstance(direct)) {
            return Optional.of(type.cast(direct));
        }

        for (Map.Entry<Class<?>, Object> entry : values.entrySet()) {
            if (type.isAssignableFrom(entry.getKey()) && type.isInstance(entry.getValue())) {
                return Optional.of(type.cast(entry.getValue()));
            }
        }
        return Optional.empty();
    }
}
