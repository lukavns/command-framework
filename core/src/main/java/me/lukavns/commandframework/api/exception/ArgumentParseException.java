package me.lukavns.commandframework.api.exception;

public class ArgumentParseException extends CommandFrameworkException {

    private static final long serialVersionUID = 1L;

    public ArgumentParseException(String message) {
        super(message);
    }

    public ArgumentParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
