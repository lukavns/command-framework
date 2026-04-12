package me.lukavns.commandframework.api.exception;

public class CommandFrameworkException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CommandFrameworkException(String message) {
        super(message);
    }

    public CommandFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
