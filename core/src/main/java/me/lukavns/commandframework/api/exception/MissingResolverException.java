package me.lukavns.commandframework.api.exception;

public class MissingResolverException extends CommandRegistrationException {

    private static final long serialVersionUID = 1L;

    public MissingResolverException(Class<?> type) {
        super("No argument resolver is registered for " + type.getName());
    }
}
