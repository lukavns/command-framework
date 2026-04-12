package me.lukavns.commandframework.bukkit.suggestion;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.lukavns.commandframework.api.command.CommandResult;
import me.lukavns.commandframework.api.exception.CommandFrameworkException;
import me.lukavns.commandframework.api.exception.ExceptionMessageResolver;
import me.lukavns.commandframework.bukkit.context.BukkitCommandContext;
import me.lukavns.commandframework.core.dispatch.DefaultCommandDispatcher;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public final class BukkitSuggestionAdapter {

    private final Plugin plugin;
    private final DefaultCommandDispatcher<CommandSender> dispatcher;
    private final ExceptionMessageResolver<CommandSender> messageResolver;

    public BukkitSuggestionAdapter(
        Plugin plugin,
        DefaultCommandDispatcher<CommandSender> dispatcher,
        ExceptionMessageResolver<CommandSender> messageResolver
    ) {
        this.plugin = plugin;
        this.dispatcher = dispatcher;
        this.messageResolver = messageResolver;
    }

    public CommandResult dispatch(CommandSender sender, String label, String[] args) {
        BukkitCommandContext context = new BukkitCommandContext(sender, label, Arrays.asList(args), providedValues());
        try {
            return this.dispatcher.dispatch(context);
        } catch (CommandFrameworkException exception) {
            String message = this.messageResolver.resolve(context, exception);
            if (message != null && !message.isEmpty()) {
                sender.sendMessage(message);
            }
            return CommandResult.failure();
        }
    }

    public List<String> suggest(CommandSender sender, String label, String[] args) {
        BukkitCommandContext context = new BukkitCommandContext(sender, label, Arrays.asList(args), providedValues());
        try {
            return this.dispatcher.suggest(context);
        } catch (RuntimeException exception) {
            return Collections.emptyList();
        }
    }

    private Map<Class<?>, Object> providedValues() {
        Map<Class<?>, Object> values = new LinkedHashMap<Class<?>, Object>();
        values.put(Plugin.class, this.plugin);
        values.put(this.plugin.getClass(), this.plugin);
        values.put(Server.class, this.plugin.getServer());
        return Collections.unmodifiableMap(values);
    }
}
