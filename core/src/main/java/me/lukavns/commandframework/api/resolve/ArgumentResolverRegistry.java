package me.lukavns.commandframework.api.resolve;

import java.util.Optional;

public interface ArgumentResolverRegistry {

    <T> void register(Class<T> type, ArgumentResolver<? extends T> resolver);

    Optional<ArgumentResolver<?>> findResolver(Class<?> type);

    boolean supports(Class<?> type);
}
