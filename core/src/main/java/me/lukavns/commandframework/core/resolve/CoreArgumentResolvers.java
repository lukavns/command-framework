package me.lukavns.commandframework.core.resolve;

import java.util.UUID;
import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.exception.ArgumentParseException;
import me.lukavns.commandframework.api.resolve.ArgumentResolver;

public final class CoreArgumentResolvers {

    private CoreArgumentResolvers() {
    }

    public static void registerDefaults(DefaultArgumentResolverRegistry registry) {
        registry.register(String.class, passThrough());
        registry.register(Integer.class, number("integer", Integer.class, new NumberParser<Integer>() {
            @Override
            public Integer parse(String input) {
                return Integer.valueOf(input);
            }
        }));
        registry.register(Integer.TYPE, number("integer", Integer.TYPE, new NumberParser<Integer>() {
            @Override
            public Integer parse(String input) {
                return Integer.valueOf(input);
            }
        }));
        registry.register(Long.class, number("long", Long.class, new NumberParser<Long>() {
            @Override
            public Long parse(String input) {
                return Long.valueOf(input);
            }
        }));
        registry.register(Long.TYPE, number("long", Long.TYPE, new NumberParser<Long>() {
            @Override
            public Long parse(String input) {
                return Long.valueOf(input);
            }
        }));
        registry.register(Double.class, number("double", Double.class, new NumberParser<Double>() {
            @Override
            public Double parse(String input) {
                return Double.valueOf(input);
            }
        }));
        registry.register(Double.TYPE, number("double", Double.TYPE, new NumberParser<Double>() {
            @Override
            public Double parse(String input) {
                return Double.valueOf(input);
            }
        }));
        registry.register(Float.class, number("float", Float.class, new NumberParser<Float>() {
            @Override
            public Float parse(String input) {
                return Float.valueOf(input);
            }
        }));
        registry.register(Float.TYPE, number("float", Float.TYPE, new NumberParser<Float>() {
            @Override
            public Float parse(String input) {
                return Float.valueOf(input);
            }
        }));
        registry.register(Boolean.class, new ArgumentResolver<Boolean>() {
            @Override
            public Boolean resolve(CommandContext<?> context, ParameterDefinition parameter, String input) {
                if ("true".equalsIgnoreCase(input) || "yes".equalsIgnoreCase(input) || "on".equalsIgnoreCase(input)) {
                    return Boolean.TRUE;
                }
                if ("false".equalsIgnoreCase(input) || "no".equalsIgnoreCase(input) || "off".equalsIgnoreCase(input)) {
                    return Boolean.FALSE;
                }
                throw new ArgumentParseException("Expected a boolean for " + parameter.name() + ", got '" + input + "'");
            }
        });
        registry.register(Boolean.TYPE, new ArgumentResolver<Boolean>() {
            @Override
            public Boolean resolve(CommandContext<?> context, ParameterDefinition parameter, String input) {
                return (Boolean) registry.findResolver(Boolean.class).get().resolve(context, parameter, input);
            }
        });
        registry.register(UUID.class, new ArgumentResolver<UUID>() {
            @Override
            public UUID resolve(CommandContext<?> context, ParameterDefinition parameter, String input) {
                try {
                    return UUID.fromString(input);
                } catch (IllegalArgumentException exception) {
                    throw new ArgumentParseException("Expected a UUID for " + parameter.name() + ", got '" + input + "'", exception);
                }
            }
        });
    }

    private static ArgumentResolver<String> passThrough() {
        return new ArgumentResolver<String>() {
            @Override
            public String resolve(CommandContext<?> context, ParameterDefinition parameter, String input) {
                return input;
            }
        };
    }

    private static <T> ArgumentResolver<T> number(
        final String label,
        final Class<?> type,
        final NumberParser<T> parser
    ) {
        return new ArgumentResolver<T>() {
            @Override
            public T resolve(CommandContext<?> context, ParameterDefinition parameter, String input) {
                try {
                    return parser.parse(input);
                } catch (RuntimeException exception) {
                    throw new ArgumentParseException("Expected a " + label + " for " + parameter.name() + ", got '" + input + "'", exception);
                }
            }
        };
    }

    private interface NumberParser<T> {
        T parse(String input);
    }
}
