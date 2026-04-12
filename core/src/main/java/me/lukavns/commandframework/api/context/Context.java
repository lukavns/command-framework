package me.lukavns.commandframework.api.context;

import java.util.List;

/**
 * Legacy-friendly command context that exposes convenience accessors and
 * sender-facing messaging operations.
 *
 * @param <S> sender type exposed by the current platform
 */
public interface Context<S> extends CommandContext<S> {

    default String getLabel() {
        return label();
    }

    default S getSender() {
        return sender();
    }

    default String[] getArgs() {
        List<String> arguments = rawArguments();
        return arguments.toArray(new String[arguments.size()]);
    }

    default int argsCount() {
        return getArgs().length;
    }

    default String getArg(int index) {
        String[] arguments = getArgs();
        return index >= 0 && index < arguments.length ? arguments[index] : null;
    }

    void sendMessage(String message);

    void sendMessage(String[] messages);
}
