package me.lukavns.commandframework.api.command;

import me.lukavns.commandframework.api.context.CommandContext;

public interface CommandHandler {

    Object invoke(CommandContext<?> context, Object[] arguments) throws Exception;

    String declaringType();

    String methodName();
}
