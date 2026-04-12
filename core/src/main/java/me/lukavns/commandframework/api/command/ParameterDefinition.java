package me.lukavns.commandframework.api.command;

import java.util.Objects;
import me.lukavns.commandframework.api.suggestion.SuggestionDescriptor;

public final class ParameterDefinition {

    private final Class<?> parameterType;
    private final String name;
    private final ParameterSourceKind sourceKind;
    private final boolean optional;
    private final String defaultValue;
    private final boolean remaining;
    private final Class<?> resolverBinding;
    private final SuggestionDescriptor suggestionDescriptor;

    private ParameterDefinition(Builder builder) {
        this.parameterType = Objects.requireNonNull(builder.parameterType, "parameterType");
        this.name = Objects.requireNonNull(builder.name, "name");
        this.sourceKind = Objects.requireNonNull(builder.sourceKind, "sourceKind");
        this.optional = builder.optional;
        this.defaultValue = builder.defaultValue;
        this.remaining = builder.remaining;
        this.resolverBinding = builder.resolverBinding == null ? this.parameterType : builder.resolverBinding;
        this.suggestionDescriptor = builder.suggestionDescriptor == null
            ? SuggestionDescriptor.none()
            : builder.suggestionDescriptor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Class<?> parameterType() {
        return this.parameterType;
    }

    public String name() {
        return this.name;
    }

    public ParameterSourceKind sourceKind() {
        return this.sourceKind;
    }

    public boolean optional() {
        return this.optional;
    }

    public String defaultValue() {
        return this.defaultValue;
    }

    public boolean remaining() {
        return this.remaining;
    }

    public Class<?> resolverBinding() {
        return this.resolverBinding;
    }

    public SuggestionDescriptor suggestionDescriptor() {
        return this.suggestionDescriptor;
    }

    public static final class Builder {

        private Class<?> parameterType;
        private String name;
        private ParameterSourceKind sourceKind = ParameterSourceKind.ARGUMENT;
        private boolean optional;
        private String defaultValue;
        private boolean remaining;
        private Class<?> resolverBinding;
        private SuggestionDescriptor suggestionDescriptor;

        private Builder() {
        }

        public Builder parameterType(Class<?> parameterType) {
            this.parameterType = parameterType;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder sourceKind(ParameterSourceKind sourceKind) {
            this.sourceKind = sourceKind;
            return this;
        }

        public Builder optional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder remaining(boolean remaining) {
            this.remaining = remaining;
            return this;
        }

        public Builder resolverBinding(Class<?> resolverBinding) {
            this.resolverBinding = resolverBinding;
            return this;
        }

        public Builder suggestionDescriptor(SuggestionDescriptor suggestionDescriptor) {
            this.suggestionDescriptor = suggestionDescriptor;
            return this;
        }

        public ParameterDefinition build() {
            return new ParameterDefinition(this);
        }
    }
}
