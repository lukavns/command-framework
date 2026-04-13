package me.lukavns.commandframework.core.dispatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.lukavns.commandframework.api.command.CommandDefinition;
import me.lukavns.commandframework.api.context.Context;

/**
 * Base implementation shared by platform-specific command contexts.
 *
 * @param <S> sender type exposed by the current platform
 */
public abstract class BaseCommandContext<S> implements Context<S> {

    private final S sender;
    private final String label;
    private final List<String> rawArguments;
    private final Map<Class<?>, Object> providedValues;
    private CommandDefinition command;

    protected BaseCommandContext(S sender, String label, List<String> rawArguments, Map<Class<?>, Object> providedValues) {
        this.sender = sender;
        this.label = label;
        this.rawArguments = Collections.unmodifiableList(new ArrayList<String>(rawArguments));
        this.providedValues = Collections.unmodifiableMap(new LinkedHashMap<Class<?>, Object>(providedValues));
    }

    @Override
    public final S sender() {
        return this.sender;
    }

    @Override
    public final String label() {
        return this.label;
    }

    @Override
    public final List<String> rawArguments() {
        return this.rawArguments;
    }

    @Override
    public Optional<CommandDefinition> command() {
        return Optional.ofNullable(this.command);
    }

    @Override
    public Map<String, Object> values() {
        return Collections.emptyMap();
    }

    @Override
    public Map<Class<?>, Object> providedValues() {
        return this.providedValues;
    }

    final void bindCommand(CommandDefinition command) {
        this.command = command;
    }
}
