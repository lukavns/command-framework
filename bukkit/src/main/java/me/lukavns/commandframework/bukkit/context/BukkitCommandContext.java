package me.lukavns.commandframework.bukkit.context;

import java.util.List;
import java.util.Map;
import me.lukavns.commandframework.core.dispatch.BaseCommandContext;
import org.bukkit.command.CommandSender;

/**
 * Bukkit-specific command context backed by a {@link CommandSender}.
 */
public final class BukkitCommandContext extends BaseCommandContext<CommandSender> {

    public BukkitCommandContext(
        CommandSender sender,
        String label,
        List<String> rawArguments,
        Map<Class<?>, Object> providedValues
    ) {
        super(sender, label, rawArguments, providedValues);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permission == null || permission.isEmpty() || sender().hasPermission(permission);
    }

    @Override
    public void sendMessage(String message) {
        sender().sendMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        sender().sendMessage(messages);
    }
}
