package me.lukavns.commandframework.bungee.context;

import java.util.List;
import java.util.Map;
import me.lukavns.commandframework.core.dispatch.BaseCommandContext;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Bungee-specific command context backed by a {@link CommandSender}.
 */
public final class BungeeCommandContext extends BaseCommandContext<CommandSender> {

    public BungeeCommandContext(
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
        sender().sendMessage(new TextComponent(message));
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }
}
