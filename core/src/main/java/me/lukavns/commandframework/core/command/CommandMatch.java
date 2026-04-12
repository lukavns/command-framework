package me.lukavns.commandframework.core.command;

import me.lukavns.commandframework.api.command.CommandDefinition;

public final class CommandMatch {

    private final CommandDefinition definition;
    private final int consumedPathSegments;

    public CommandMatch(CommandDefinition definition, int consumedPathSegments) {
        this.definition = definition;
        this.consumedPathSegments = consumedPathSegments;
    }

    public CommandDefinition definition() {
        return this.definition;
    }

    public int consumedPathSegments() {
        return this.consumedPathSegments;
    }
}
