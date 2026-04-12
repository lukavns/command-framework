package me.lukavns.commandframework.api.suggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.lukavns.commandframework.api.command.CommandDefinition;
import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.context.CommandContext;

public final class SuggestionContext<S> {

    private final CommandContext<S> commandContext;
    private final CommandDefinition command;
    private final ParameterDefinition parameter;
    private final List<String> consumedArguments;
    private final String currentInput;

    public SuggestionContext(
        CommandContext<S> commandContext,
        CommandDefinition command,
        ParameterDefinition parameter,
        List<String> consumedArguments,
        String currentInput
    ) {
        this.commandContext = commandContext;
        this.command = command;
        this.parameter = parameter;
        this.consumedArguments = Collections.unmodifiableList(new ArrayList<String>(consumedArguments));
        this.currentInput = currentInput == null ? "" : currentInput;
    }

    public CommandContext<S> commandContext() {
        return this.commandContext;
    }

    public CommandDefinition command() {
        return this.command;
    }

    public ParameterDefinition parameter() {
        return this.parameter;
    }

    public List<String> consumedArguments() {
        return this.consumedArguments;
    }

    public String currentInput() {
        return this.currentInput;
    }
}
