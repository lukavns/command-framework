package me.lukavns.commandframework.core.dispatch;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.lukavns.commandframework.api.command.CommandDefinition;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.context.Context;

/**
 * Decorates an existing context with resolved command metadata and parsed values.
 *
 * @param <S> sender type exposed by the current platform
 */
final class ResolvedCommandContext<S> implements Context<S> {

    private final CommandContext<S> delegate;
    private final CommandDefinition command;
    private final List<String> rawArguments;
    private final Map<String, Object> values;

    ResolvedCommandContext(CommandContext<S> delegate, CommandDefinition command, List<String> rawArguments, Map<String, Object> values) {
        this.delegate = delegate;
        this.command = command;
        this.rawArguments = Collections.unmodifiableList(rawArguments);
        this.values = Collections.unmodifiableMap(new LinkedHashMap<String, Object>(values));
    }

    @Override
    public S sender() {
        return this.delegate.sender();
    }

    @Override
    public String label() {
        return this.delegate.label();
    }

    @Override
    public List<String> rawArguments() {
        return this.rawArguments;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.delegate.hasPermission(permission);
    }

    @Override
    public Optional<CommandDefinition> command() {
        return Optional.of(this.command);
    }

    @Override
    public Map<String, Object> values() {
        return this.values;
    }

    @Override
    public Map<Class<?>, Object> providedValues() {
        Map<Class<?>, Object> values = new LinkedHashMap<Class<?>, Object>(this.delegate.providedValues());
        values.put(CommandContext.class, this);
        values.put(Context.class, this);
        return Collections.unmodifiableMap(values);
    }

    @Override
    public void sendMessage(String message) {
        if (this.delegate instanceof Context) {
            ((Context<S>) this.delegate).sendMessage(message);
            return;
        }
        throw new UnsupportedOperationException("This command context does not support sendMessage(String)");
    }

    @Override
    public void sendMessage(String[] messages) {
        if (this.delegate instanceof Context) {
            ((Context<S>) this.delegate).sendMessage(messages);
            return;
        }
        throw new UnsupportedOperationException("This command context does not support sendMessage(String[])");
    }
}
