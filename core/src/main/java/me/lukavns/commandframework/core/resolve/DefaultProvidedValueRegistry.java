package me.lukavns.commandframework.core.resolve;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.context.ProvidedValueFactory;

public final class DefaultProvidedValueRegistry {

    private final Map<Class<?>, ProvidedValueFactory<?>> factories = new LinkedHashMap<Class<?>, ProvidedValueFactory<?>>();

    public <T> void register(Class<T> type, ProvidedValueFactory<? extends T> factory) {
        this.factories.put(type, factory);
    }

    public boolean supports(Class<?> type) {
        if (CommandContext.class.isAssignableFrom(type)) {
            return true;
        }
        return this.factories.containsKey(type);
    }

    public Optional<Object> provide(Class<?> type, CommandContext<?> context) {
        if (CommandContext.class.isAssignableFrom(type)) {
            return Optional.<Object>of(context);
        }
        ProvidedValueFactory<?> factory = this.factories.get(type);
        if (factory == null) {
            return Optional.empty();
        }
        return Optional.<Object>ofNullable(factory.provide(context));
    }
}
