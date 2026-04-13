package me.lukavns.commandframework.api.exception;

public enum MessageType {

    INCORRECT_USAGE("§cCorrect usage: /{usage}"),
    MISSING_PERMISSION("§cYou do not have permission to use this command."),
    INCORRECT_TARGET("§cThis command can only be executed by {target}."),
    INTERNAL_ERROR("§cAn internal command error occurred. Check the logs for details.");

    private final String defaultMessage;

    MessageType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String defaultMessage() {
        return this.defaultMessage;
    }
}
