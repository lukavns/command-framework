package me.lukavns.commandframework.api.exception;

public class CommandRegistrationException extends CommandFrameworkException {

    private static final long serialVersionUID = 1L;

    public CommandRegistrationException(String message) {
        super(message);
    }

    public CommandRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
