package me.lukavns.commandframework.core.parse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.lukavns.commandframework.api.command.CommandDefinition;
import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.command.ParameterSourceKind;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.exception.ArgumentParseException;
import me.lukavns.commandframework.api.exception.CommandFrameworkException;
import me.lukavns.commandframework.api.exception.MissingResolverException;
import me.lukavns.commandframework.api.resolve.ArgumentResolver;
import me.lukavns.commandframework.core.resolve.DefaultArgumentResolverRegistry;
import me.lukavns.commandframework.core.resolve.DefaultProvidedValueRegistry;

public final class ArgumentParser {

    private static final Object CONTEXT_SENTINEL = new Object();

    private final DefaultArgumentResolverRegistry resolverRegistry;
    private final DefaultProvidedValueRegistry providedValueRegistry;

    public ArgumentParser(
        DefaultArgumentResolverRegistry resolverRegistry,
        DefaultProvidedValueRegistry providedValueRegistry
    ) {
        this.resolverRegistry = resolverRegistry;
        this.providedValueRegistry = providedValueRegistry;
    }

    public ParsedInvocation parse(CommandContext<?> context, CommandDefinition definition, int consumedSegments) {
        List<String> arguments = context.rawArguments().subList(consumedSegments, context.rawArguments().size());
        List<Object> invocationArguments = new ArrayList<Object>();
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        int cursor = 0;

        for (ParameterDefinition parameter : definition.parameters()) {
            Object value;
            if (parameter.sourceKind() == ParameterSourceKind.SENDER) {
                value = resolveSender(context, parameter);
            } else if (parameter.sourceKind() == ParameterSourceKind.PROVIDED) {
                value = resolveProvided(context, parameter);
            } else {
                if (parameter.remaining()) {
                    String joined = join(arguments, cursor);
                    if (joined == null || joined.isEmpty()) {
                        if (!parameter.optional()) {
                            throw new ArgumentParseException("Missing required argument <" + parameter.name() + ">");
                        }
                        value = defaultValue(context, parameter);
                    } else {
                        value = resolveArgument(context, parameter, joined);
                        cursor = arguments.size();
                    }
                } else {
                    if (cursor >= arguments.size()) {
                        if (!parameter.optional()) {
                            throw new ArgumentParseException("Missing required argument <" + parameter.name() + ">");
                        }
                        value = defaultValue(context, parameter);
                    } else {
                        value = resolveArgument(context, parameter, arguments.get(cursor));
                        cursor++;
                    }
                }
            }

            invocationArguments.add(value);
            values.put(parameter.name(), value == CONTEXT_SENTINEL ? null : value);
        }

        return new ParsedInvocation(invocationArguments.toArray(new Object[0]), values);
    }

    public Object[] resolveContextSentinel(Object[] arguments, CommandContext<?> resolvedContext) {
        Object[] resolved = new Object[arguments.length];
        for (int index = 0; index < arguments.length; index++) {
            resolved[index] = arguments[index] == CONTEXT_SENTINEL ? resolvedContext : arguments[index];
        }
        return resolved;
    }

    private Object resolveSender(CommandContext<?> context, ParameterDefinition parameter) {
        try {
            return context.senderAs(parameter.parameterType());
        } catch (IllegalArgumentException exception) {
            throw new ArgumentParseException("Sender is not compatible with " + parameter.parameterType().getName(), exception);
        }
    }

    private Object resolveProvided(CommandContext<?> context, ParameterDefinition parameter) {
        if (CommandContext.class.isAssignableFrom(parameter.parameterType())) {
            return CONTEXT_SENTINEL;
        }
        Optional<Object> provided = this.providedValueRegistry.provide(parameter.parameterType(), context);
        if (!provided.isPresent()) {
            throw new ArgumentParseException("No provided value is available for " + parameter.parameterType().getName());
        }
        return provided.get();
    }

    private Object defaultValue(CommandContext<?> context, ParameterDefinition parameter) {
        if (parameter.defaultValue() == null) {
            return null;
        }
        return resolveArgument(context, parameter, parameter.defaultValue());
    }

    private Object resolveArgument(CommandContext<?> context, ParameterDefinition parameter, String input) {
        Optional<ArgumentResolver<?>> resolver = this.resolverRegistry.findResolver(parameter.resolverBinding());
        if (!resolver.isPresent()) {
            throw new MissingResolverException(parameter.resolverBinding());
        }
        try {
            return resolver.get().resolve(context, parameter, input);
        } catch (CommandFrameworkException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ArgumentParseException("Could not parse '" + input + "' for " + parameter.name(), exception);
        }
    }

    private String join(List<String> arguments, int cursor) {
        if (cursor >= arguments.size()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int index = cursor; index < arguments.size(); index++) {
            if (index > cursor) {
                builder.append(' ');
            }
            builder.append(arguments.get(index));
        }
        return builder.toString();
    }
}
