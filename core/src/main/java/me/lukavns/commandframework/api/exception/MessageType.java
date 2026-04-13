package me.lukavns.commandframework.api.exception;

public final class MessageType {

    public static final ExceptionMessageType INCORRECT_USAGE = ExceptionMessageType.INCORRECT_USAGE;
    public static final ExceptionMessageType MISSING_PERMISSION = ExceptionMessageType.MISSING_PERMISSION;
    public static final ExceptionMessageType INCORRECT_TARGET = ExceptionMessageType.INCORRECT_TARGET;
    public static final ExceptionMessageType INTERNAL_ERROR = ExceptionMessageType.INTERNAL_ERROR;

    private MessageType() {}
}
