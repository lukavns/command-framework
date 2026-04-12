package me.lukavns.commandframework.api.exception;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class InvalidCommandSenderException extends CommandFrameworkException {

    private static final long serialVersionUID = 1L;

    private final List<Class<?>> supportedTypes;

    public InvalidCommandSenderException(List<Class<?>> supportedTypes) {
        super("Unsupported command sender. Expected one of: " + supportedTypes);
        this.supportedTypes = Collections.unmodifiableList(new ArrayList<Class<?>>(supportedTypes));
    }

    public List<Class<?>> supportedTypes() {
        return this.supportedTypes;
    }
}
