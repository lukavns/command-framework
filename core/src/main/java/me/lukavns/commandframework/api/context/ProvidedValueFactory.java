package me.lukavns.commandframework.api.context;

public interface ProvidedValueFactory<T> {

    Class<T> type();

    T provide(CommandContext<?> context);
}
