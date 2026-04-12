package me.lukavns.commandframework.core.internal;

import java.util.LinkedHashSet;
import java.util.Set;
import me.lukavns.commandframework.api.command.ParameterSourceKind;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.core.resolve.DefaultProvidedValueRegistry;

public final class ParameterClassifier {

    private final Set<Class<?>> senderTypes;
    private final DefaultProvidedValueRegistry providedValueRegistry;

    public ParameterClassifier(Set<Class<?>> senderTypes, DefaultProvidedValueRegistry providedValueRegistry) {
        this.senderTypes = new LinkedHashSet<Class<?>>(senderTypes);
        this.providedValueRegistry = providedValueRegistry;
    }

    public ParameterSourceKind classify(Class<?> type) {
        if (CommandContext.class.isAssignableFrom(type) || this.providedValueRegistry.supports(type)) {
            return ParameterSourceKind.PROVIDED;
        }
        if (this.senderTypes.contains(type)) {
            return ParameterSourceKind.SENDER;
        }
        return ParameterSourceKind.ARGUMENT;
    }
}
