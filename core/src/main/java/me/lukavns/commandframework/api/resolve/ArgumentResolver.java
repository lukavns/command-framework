package me.lukavns.commandframework.api.resolve;

import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.context.CommandContext;

public interface ArgumentResolver<T> {

    T resolve(CommandContext<?> context, ParameterDefinition parameter, String input);
}
