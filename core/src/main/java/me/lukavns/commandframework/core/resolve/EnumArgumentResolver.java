package me.lukavns.commandframework.core.resolve;

import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.exception.ArgumentParseException;
import me.lukavns.commandframework.api.resolve.ArgumentResolver;

final class EnumArgumentResolver implements ArgumentResolver<Enum<?>> {

    private final Class<? extends Enum<?>> enumType;

    EnumArgumentResolver(Class<? extends Enum<?>> enumType) {
        this.enumType = enumType;
    }

    @Override
    public Enum<?> resolve(CommandContext<?> context, ParameterDefinition parameter, String input) {
        for (Enum<?> constant : this.enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(input)) {
                return constant;
            }
        }
        throw new ArgumentParseException("Unknown " + parameter.name() + ": " + input);
    }
}
