package me.lukavns.commandframework.api.command;

import java.util.Objects;

public final class CommandResult {

    public enum Status {
        SUCCESS,
        FAILURE,
        NO_MATCH
    }

    private static final CommandResult SUCCESS = new CommandResult(Status.SUCCESS);
    private static final CommandResult FAILURE = new CommandResult(Status.FAILURE);
    private static final CommandResult NO_MATCH = new CommandResult(Status.NO_MATCH);

    private final Status status;

    private CommandResult(Status status) {
        this.status = Objects.requireNonNull(status, "status");
    }

    public static CommandResult success() {
        return SUCCESS;
    }

    public static CommandResult failure() {
        return FAILURE;
    }

    public static CommandResult noMatch() {
        return NO_MATCH;
    }

    public static CommandResult fromHandlerReturn(Object result) {
        if (result == null) {
            return success();
        }
        if (result instanceof CommandResult) {
            return (CommandResult) result;
        }
        if (result instanceof Boolean) {
            return ((Boolean) result).booleanValue() ? success() : failure();
        }
        throw new IllegalArgumentException("Unsupported command return type: " + result.getClass().getName());
    }

    public Status status() {
        return this.status;
    }

    public boolean isHandled() {
        return this.status != Status.NO_MATCH;
    }

    public boolean isSuccess() {
        return this.status == Status.SUCCESS;
    }

    public boolean isFailure() {
        return this.status == Status.FAILURE;
    }

    public boolean isNoMatch() {
        return this.status == Status.NO_MATCH;
    }
}
