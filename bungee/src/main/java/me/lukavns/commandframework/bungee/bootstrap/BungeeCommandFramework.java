package me.lukavns.commandframework.bungee.bootstrap;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import me.lukavns.commandframework.api.CommandScanner;
import me.lukavns.commandframework.api.command.CommandDefinition;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.context.ProvidedValueFactory;
import me.lukavns.commandframework.api.exception.CommandMessageHolder;
import me.lukavns.commandframework.api.exception.ExceptionMessageResolver;
import me.lukavns.commandframework.bungee.internal.BungeeExceptionMessenger;
import me.lukavns.commandframework.bungee.registration.BungeeCommandRegistrar;
import me.lukavns.commandframework.bungee.resolve.BungeeArgumentResolvers;
import me.lukavns.commandframework.bungee.suggestion.BungeeSuggestionAdapter;
import me.lukavns.commandframework.core.dispatch.DefaultCommandDispatcher;
import me.lukavns.commandframework.core.parse.ArgumentParser;
import me.lukavns.commandframework.core.reflect.AnnotationCommandScanner;
import me.lukavns.commandframework.core.registration.CommandRegistry;
import me.lukavns.commandframework.core.resolve.CoreArgumentResolvers;
import me.lukavns.commandframework.core.resolve.DefaultArgumentResolverRegistry;
import me.lukavns.commandframework.core.resolve.DefaultProvidedValueRegistry;
import me.lukavns.commandframework.core.suggestion.DefaultSuggestionRegistry;
import me.lukavns.commandframework.core.suggestion.SuggestionEngine;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeCommandFramework {

    private final Plugin plugin;
    private final DefaultArgumentResolverRegistry resolverRegistry;
    private final DefaultProvidedValueRegistry providedValueRegistry;
    private final DefaultSuggestionRegistry<CommandSender> suggestionRegistry;
    private final CommandScanner commandScanner;
    private final DefaultCommandDispatcher<CommandSender> dispatcher;
    private final CommandMessageHolder messageHolder;
    private final BungeeCommandRegistrar registrar;

    private BungeeCommandFramework(
        Plugin plugin,
        Executor asyncExecutor,
        ExceptionMessageResolver<CommandSender> messageResolver
    ) {
        this.plugin = plugin;
        this.resolverRegistry = new DefaultArgumentResolverRegistry();
        this.providedValueRegistry = new DefaultProvidedValueRegistry();
        this.suggestionRegistry = new DefaultSuggestionRegistry<CommandSender>();
        this.messageHolder = new CommandMessageHolder();

        CoreArgumentResolvers.registerDefaults(this.resolverRegistry);
        BungeeArgumentResolvers.registerDefaults(this.resolverRegistry, this.plugin.getProxy());
        registerProvidedValues();

        Set<Class<?>> senderTypes = new LinkedHashSet<Class<?>>(Arrays.<Class<?>>asList(CommandSender.class));
        this.commandScanner = new AnnotationCommandScanner(this.resolverRegistry, this.providedValueRegistry, senderTypes);

        CommandRegistry commandRegistry = new CommandRegistry();
        this.dispatcher = new DefaultCommandDispatcher<CommandSender>(
            commandRegistry,
            new ArgumentParser(this.resolverRegistry, this.providedValueRegistry),
            new SuggestionEngine<CommandSender>(this.suggestionRegistry)
        );
        this.dispatcher.asyncExecutor(asyncExecutor);

        BungeeSuggestionAdapter adapter = new BungeeSuggestionAdapter(
            this.plugin,
            this.dispatcher,
            messageResolver == null ? new BungeeExceptionMessenger(this.messageHolder) : messageResolver
        );
        this.registrar = new BungeeCommandRegistrar(this.plugin, commandRegistry, adapter);
    }

    public static Builder builder(Plugin plugin) {
        return new Builder(plugin);
    }

    public void register(Object... commandHolders) {
        List<CommandDefinition> definitions = this.commandScanner.scan(commandHolders);
        this.dispatcher.register(definitions);
        this.registrar.sync();
    }

    public DefaultArgumentResolverRegistry resolvers() {
        return this.resolverRegistry;
    }

    public DefaultProvidedValueRegistry providedValues() {
        return this.providedValueRegistry;
    }

    public DefaultSuggestionRegistry<CommandSender> suggestions() {
        return this.suggestionRegistry;
    }

    public DefaultCommandDispatcher<CommandSender> dispatcher() {
        return this.dispatcher;
    }

    public CommandMessageHolder messages() {
        return this.messageHolder;
    }

    public CommandMessageHolder messageHolder() {
        return this.messageHolder;
    }

    public BungeeCommandRegistrar commandRegistrar() {
        return this.registrar;
    }

    private void registerProvidedValues() {
        this.providedValueRegistry.register(Plugin.class, new ProvidedValueFactory<Plugin>() {
            @Override
            public Class<Plugin> type() {
                return Plugin.class;
            }

            @Override
            public Plugin provide(CommandContext<?> context) {
                return BungeeCommandFramework.this.plugin;
            }
        });
        this.providedValueRegistry.register(ProxyServer.class, new ProvidedValueFactory<ProxyServer>() {
            @Override
            public Class<ProxyServer> type() {
                return ProxyServer.class;
            }

            @Override
            public ProxyServer provide(CommandContext<?> context) {
                return BungeeCommandFramework.this.plugin.getProxy();
            }
        });
        registerRuntimePluginType();
    }

    @SuppressWarnings("unchecked")
    private void registerRuntimePluginType() {
        this.providedValueRegistry.register((Class<Plugin>) this.plugin.getClass(), new ProvidedValueFactory<Plugin>() {
            @Override
            public Class<Plugin> type() {
                return (Class<Plugin>) BungeeCommandFramework.this.plugin.getClass();
            }

            @Override
            public Plugin provide(CommandContext<?> context) {
                return BungeeCommandFramework.this.plugin;
            }
        });
    }

    public static final class Builder {

        private final Plugin plugin;
        private Executor asyncExecutor;
        private ExceptionMessageResolver<CommandSender> messageResolver;

        private Builder(Plugin plugin) {
            this.plugin = plugin;
        }

        public Builder asyncExecutor(Executor asyncExecutor) {
            this.asyncExecutor = asyncExecutor;
            return this;
        }

        public Builder messageResolver(ExceptionMessageResolver<CommandSender> messageResolver) {
            this.messageResolver = messageResolver;
            return this;
        }

        public BungeeCommandFramework build() {
            return new BungeeCommandFramework(this.plugin, this.asyncExecutor, this.messageResolver);
        }
    }
}
