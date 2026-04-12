package me.lukavns.commandframework.velocity.suggestion;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.lukavns.commandframework.api.command.CommandResult;
import me.lukavns.commandframework.api.exception.CommandFrameworkException;
import me.lukavns.commandframework.api.exception.ExceptionMessageResolver;
import me.lukavns.commandframework.core.dispatch.DefaultCommandDispatcher;
import me.lukavns.commandframework.velocity.context.VelocityCommandContext;
import net.kyori.adventure.text.Component;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public final class VelocitySuggestionAdapter {

    private final ProxyServer proxyServer;
    private final Object plugin;
    private final DefaultCommandDispatcher<CommandSource> dispatcher;
    private final ExceptionMessageResolver<CommandSource> messageResolver;

    public VelocitySuggestionAdapter(
        ProxyServer proxyServer,
        Object plugin,
        DefaultCommandDispatcher<CommandSource> dispatcher,
        ExceptionMessageResolver<CommandSource> messageResolver
    ) {
        this.proxyServer = proxyServer;
        this.plugin = plugin;
        this.dispatcher = dispatcher;
        this.messageResolver = messageResolver;
    }

    public CommandResult dispatch(CommandSource sender, String label, String[] args) {
        VelocityCommandContext context = new VelocityCommandContext(sender, label, Arrays.asList(args), providedValues());
        try {
            return this.dispatcher.dispatch(context);
        } catch (CommandFrameworkException exception) {
            String message = this.messageResolver.resolve(context, exception);
            if (message != null && !message.isEmpty()) {
                sender.sendMessage(Component.text(message));
            }
            return CommandResult.failure();
        }
    }

    public List<String> suggest(CommandSource sender, String label, String[] args) {
        VelocityCommandContext context = new VelocityCommandContext(sender, label, Arrays.asList(args), providedValues());
        try {
            return this.dispatcher.suggest(context);
        } catch (RuntimeException exception) {
            return Collections.emptyList();
        }
    }

    private Map<Class<?>, Object> providedValues() {
        Map<Class<?>, Object> values = new LinkedHashMap<Class<?>, Object>();
        values.put(ProxyServer.class, this.proxyServer);
        values.put(this.plugin.getClass(), this.plugin);
        return Collections.unmodifiableMap(values);
    }
}
