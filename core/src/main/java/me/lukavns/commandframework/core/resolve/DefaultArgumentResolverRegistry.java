package me.lukavns.commandframework.core.resolve;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import me.lukavns.commandframework.api.resolve.ArgumentResolver;
import me.lukavns.commandframework.api.resolve.ArgumentResolverRegistry;

public final class DefaultArgumentResolverRegistry implements ArgumentResolverRegistry {

    private final Map<Class<?>, ArgumentResolver<?>> resolvers = new LinkedHashMap<Class<?>, ArgumentResolver<?>>();

    @Override
    public <T> void register(Class<T> type, ArgumentResolver<? extends T> resolver) {
        this.resolvers.put(type, resolver);
    }

    @Override
    public Optional<ArgumentResolver<?>> findResolver(Class<?> type) {
        ArgumentResolver<?> resolver = this.resolvers.get(type);
        if (resolver != null) {
            return Optional.of(resolver);
        }
        if (type.isEnum()) {
            return Optional.<ArgumentResolver<?>>of(new EnumArgumentResolver(enumType(type)));
        }
        return Optional.empty();
    }

    @Override
    public boolean supports(Class<?> type) {
        return findResolver(type).isPresent();
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Enum<?>> enumType(Class<?> type) {
        return (Class<? extends Enum<?>>) type;
    }
}
