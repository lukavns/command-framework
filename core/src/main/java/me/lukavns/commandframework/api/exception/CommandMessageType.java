package me.lukavns.commandframework.api.exception;

public enum CommandMessageType {

    INCORRECT_USAGE("Correct usage: /{usage}"),
    NO_PERMISSION("You do not have permission to use this command."),
    INCORRECT_TARGET("This command can only be executed by {target}."),
    ERROR("An internal command error occurred. Check the logs for details.");

    private final String defaultMessage;

    CommandMessageType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String defaultMessage() {
        return this.defaultMessage;
    }
}
