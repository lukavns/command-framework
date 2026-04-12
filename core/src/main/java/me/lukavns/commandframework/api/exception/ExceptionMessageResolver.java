package me.lukavns.commandframework.api.exception;

import me.lukavns.commandframework.api.context.CommandContext;

public interface ExceptionMessageResolver<S> {

    String resolve(CommandContext<S> context, CommandFrameworkException exception);
}
