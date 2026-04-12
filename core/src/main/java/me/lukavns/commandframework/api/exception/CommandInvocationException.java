package me.lukavns.commandframework.api.exception;

public class CommandInvocationException extends CommandFrameworkException {

    private static final long serialVersionUID = 1L;

    public CommandInvocationException(String message) {
        super(message);
    }

    public CommandInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
