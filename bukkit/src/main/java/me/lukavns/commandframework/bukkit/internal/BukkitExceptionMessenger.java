package me.lukavns.commandframework.bukkit.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.exception.ArgumentParseException;
import me.lukavns.commandframework.api.exception.CommandFrameworkException;
import me.lukavns.commandframework.api.exception.ExceptionMessageHolder;
import me.lukavns.commandframework.api.exception.ExceptionMessageResolver;
import me.lukavns.commandframework.api.exception.InvalidCommandSenderException;
import me.lukavns.commandframework.api.exception.MessageType;
import me.lukavns.commandframework.api.exception.PermissionDeniedException;
import org.bukkit.command.CommandSender;

public final class BukkitExceptionMessenger implements ExceptionMessageResolver<CommandSender> {

    private final ExceptionMessageHolder messageHolder;

    public BukkitExceptionMessenger(ExceptionMessageHolder messageHolder) {
        this.messageHolder = messageHolder;
    }

    @Override
    public String resolve(CommandContext<CommandSender> context, CommandFrameworkException exception) {
        if (exception instanceof PermissionDeniedException) {
            PermissionDeniedException permissionException = (PermissionDeniedException) exception;
            return this.messageHolder.render(
                MessageType.MISSING_PERMISSION,
                placeholders(context, exception, permissionException.permission(), null)
            );
        }
        if (exception instanceof InvalidCommandSenderException) {
            InvalidCommandSenderException senderException = (InvalidCommandSenderException) exception;
            return this.messageHolder.render(
                MessageType.INCORRECT_TARGET,
                placeholders(context, exception, null, describeSenderTypes(senderException.supportedTypes()))
            );
        }
        if (exception instanceof ArgumentParseException) {
            return this.messageHolder.render(MessageType.INCORRECT_USAGE, placeholders(context, exception, null, null));
        }
        return this.messageHolder.render(MessageType.INTERNAL_ERROR, placeholders(context, exception, null, null));
    }

    private Map<String, String> placeholders(
        CommandContext<CommandSender> context,
        CommandFrameworkException exception,
        String permission,
        String target
    ) {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put("usage", usage(context));
        values.put("permission", permission == null ? "" : permission);
        values.put("target", target == null ? "" : target);
        values.put("error", exception.getMessage() == null ? "" : exception.getMessage());
        return values;
    }

    private String usage(CommandContext<CommandSender> context) {
        if (context.command().isPresent()) {
            String usage = context.command().get().usage();
            if (usage != null && !usage.isEmpty()) {
                return usage;
            }
            return context.command().get().fullPath();
        }
        return context.label();
    }

    private String describeSenderTypes(List<Class<?>> supportedTypes) {
        LinkedHashSet<String> descriptions = new LinkedHashSet<String>();
        for (Class<?> supportedType : supportedTypes) {
            descriptions.add(describeSenderType(supportedType));
        }

        if (descriptions.isEmpty()) {
            return "the supported senders";
        }

        List<String> values = new ArrayList<String>(descriptions);
        if (values.size() == 1) {
            return values.get(0);
        }
        if (values.size() == 2) {
            return values.get(0) + " or " + values.get(1);
        }

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                builder.append(index == values.size() - 1 ? ", or " : ", ");
            }
            builder.append(values.get(index));
        }
        return builder.toString();
    }

    private String describeSenderType(Class<?> supportedType) {
        String typeName = supportedType.getName();
        if (typeName.endsWith(".Player") || typeName.endsWith(".ProxiedPlayer")) {
            return "players";
        }
        if (typeName.contains("Console")) {
            return "the console";
        }
        if (typeName.endsWith(".CommandSender") || typeName.endsWith(".CommandSource")) {
            return "command senders";
        }
        return supportedType.getSimpleName();
    }
}
