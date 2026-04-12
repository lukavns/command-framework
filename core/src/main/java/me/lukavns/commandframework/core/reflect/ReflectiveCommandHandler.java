package me.lukavns.commandframework.core.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import me.lukavns.commandframework.api.command.CommandHandler;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.exception.CommandInvocationException;

public final class ReflectiveCommandHandler implements CommandHandler {

    private final Object instance;
    private final Method method;

    public ReflectiveCommandHandler(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
        this.method.setAccessible(true);
    }

    @Override
    public Object invoke(CommandContext<?> context, Object[] arguments) throws Exception {
        try {
            return this.method.invoke(this.instance, arguments);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause() == null ? exception : exception.getCause();
            if (cause instanceof Exception) {
                throw (Exception) cause;
            }
            throw new CommandInvocationException("Command handler threw an unrecoverable error", cause);
        } catch (IllegalAccessException exception) {
            throw new CommandInvocationException("Could not invoke " + declaringType() + "#" + methodName(), exception);
        }
    }

    @Override
    public String declaringType() {
        return this.method.getDeclaringClass().getName();
    }

    @Override
    public String methodName() {
        return this.method.getName();
    }
}
