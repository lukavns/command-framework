package me.lukavns.commandframework.api.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CommandDefinition {

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final String usage;
    private final String permission;
    private final List<Class<?>> senderTargets;
    private final boolean async;
    private final List<String> subcommandPath;
    private final List<ParameterDefinition> parameters;
    private final CommandHandler handler;

    private CommandDefinition(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "name");
        this.aliases = immutable(builder.aliases);
        this.description = builder.description == null ? "" : builder.description;
        this.usage = builder.usage == null ? "" : builder.usage;
        this.permission = builder.permission == null ? "" : builder.permission;
        this.senderTargets = immutable(builder.senderTargets);
        this.async = builder.async;
        this.subcommandPath = immutable(builder.subcommandPath);
        this.parameters = immutable(builder.parameters);
        this.handler = Objects.requireNonNull(builder.handler, "handler");
    }

    public static Builder builder() {
        return new Builder();
    }

    public String name() {
        return this.name;
    }

    public List<String> aliases() {
        return this.aliases;
    }

    public String description() {
        return this.description;
    }

    public String usage() {
        return this.usage;
    }

    public String permission() {
        return this.permission;
    }

    public List<Class<?>> senderTargets() {
        return this.senderTargets;
    }

    public boolean async() {
        return this.async;
    }

    public List<String> subcommandPath() {
        return this.subcommandPath;
    }

    public List<ParameterDefinition> parameters() {
        return this.parameters;
    }

    public CommandHandler handler() {
        return this.handler;
    }

    public String fullPath() {
        if (this.subcommandPath.isEmpty()) {
            return this.name;
        }
        StringBuilder builder = new StringBuilder(this.name);
        for (String segment : this.subcommandPath) {
            builder.append(' ').append(segment);
        }
        return builder.toString();
    }

    private static <T> List<T> immutable(List<T> source) {
        return Collections.unmodifiableList(new ArrayList<T>(source));
    }

    public static final class Builder {

        private String name;
        private List<String> aliases = Collections.emptyList();
        private String description;
        private String usage;
        private String permission;
        private List<Class<?>> senderTargets = Collections.emptyList();
        private boolean async;
        private List<String> subcommandPath = Collections.emptyList();
        private List<ParameterDefinition> parameters = Collections.emptyList();
        private CommandHandler handler;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder aliases(List<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder usage(String usage) {
            this.usage = usage;
            return this;
        }

        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        public Builder senderTargets(List<Class<?>> senderTargets) {
            this.senderTargets = senderTargets;
            return this;
        }

        public Builder async(boolean async) {
            this.async = async;
            return this;
        }

        public Builder subcommandPath(List<String> subcommandPath) {
            this.subcommandPath = subcommandPath;
            return this;
        }

        public Builder parameters(List<ParameterDefinition> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder handler(CommandHandler handler) {
            this.handler = handler;
            return this;
        }

        public CommandDefinition build() {
            return new CommandDefinition(this);
        }
    }
}
