package me.lukavns.commandframework.velocity.bootstrap;

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
import me.lukavns.commandframework.core.dispatch.DefaultCommandDispatcher;
import me.lukavns.commandframework.core.parse.ArgumentParser;
import me.lukavns.commandframework.core.reflect.AnnotationCommandScanner;
import me.lukavns.commandframework.core.registration.CommandRegistry;
import me.lukavns.commandframework.core.resolve.CoreArgumentResolvers;
import me.lukavns.commandframework.core.resolve.DefaultArgumentResolverRegistry;
import me.lukavns.commandframework.core.resolve.DefaultProvidedValueRegistry;
import me.lukavns.commandframework.core.suggestion.DefaultSuggestionRegistry;
import me.lukavns.commandframework.core.suggestion.SuggestionEngine;
import me.lukavns.commandframework.velocity.internal.VelocityExceptionMessenger;
import me.lukavns.commandframework.velocity.registration.VelocityCommandRegistrar;
import me.lukavns.commandframework.velocity.resolve.VelocityArgumentResolvers;
import me.lukavns.commandframework.velocity.suggestion.VelocitySuggestionAdapter;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public final class VelocityCommandFramework {

    private final ProxyServer proxyServer;
    private final Object plugin;
    private final DefaultArgumentResolverRegistry resolverRegistry;
    private final DefaultProvidedValueRegistry providedValueRegistry;
    private final DefaultSuggestionRegistry<CommandSource> suggestionRegistry;
    private final CommandScanner commandScanner;
    private final DefaultCommandDispatcher<CommandSource> dispatcher;
    private final CommandMessageHolder messageHolder;
    private final VelocityCommandRegistrar registrar;

    private VelocityCommandFramework(
        ProxyServer proxyServer,
        Object plugin,
        Executor asyncExecutor,
        ExceptionMessageResolver<CommandSource> messageResolver
    ) {
        this.proxyServer = proxyServer;
        this.plugin = plugin;
        this.resolverRegistry = new DefaultArgumentResolverRegistry();
        this.providedValueRegistry = new DefaultProvidedValueRegistry();
        this.suggestionRegistry = new DefaultSuggestionRegistry<CommandSource>();
        this.messageHolder = new CommandMessageHolder();

        CoreArgumentResolvers.registerDefaults(this.resolverRegistry);
        VelocityArgumentResolvers.registerDefaults(this.resolverRegistry, this.proxyServer);
        registerProvidedValues();

        Set<Class<?>> senderTypes = new LinkedHashSet<Class<?>>(Arrays.<Class<?>>asList(CommandSource.class));
        this.commandScanner = new AnnotationCommandScanner(this.resolverRegistry, this.providedValueRegistry, senderTypes);

        CommandRegistry commandRegistry = new CommandRegistry();
        this.dispatcher = new DefaultCommandDispatcher<CommandSource>(
            commandRegistry,
            new ArgumentParser(this.resolverRegistry, this.providedValueRegistry),
            new SuggestionEngine<CommandSource>(this.suggestionRegistry)
        );
        this.dispatcher.asyncExecutor(asyncExecutor);

        VelocitySuggestionAdapter adapter = new VelocitySuggestionAdapter(
            this.proxyServer,
            this.plugin,
            this.dispatcher,
            messageResolver == null ? new VelocityExceptionMessenger(this.messageHolder) : messageResolver
        );
        this.registrar = new VelocityCommandRegistrar(this.proxyServer, this.plugin, commandRegistry, adapter);
    }

    public static Builder builder(ProxyServer proxyServer, Object plugin) {
        return new Builder(proxyServer, plugin);
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

    public DefaultSuggestionRegistry<CommandSource> suggestions() {
        return this.suggestionRegistry;
    }

    public DefaultCommandDispatcher<CommandSource> dispatcher() {
        return this.dispatcher;
    }

    public CommandMessageHolder messages() {
        return this.messageHolder;
    }

    public CommandMessageHolder messageHolder() {
        return this.messageHolder;
    }

    public VelocityCommandRegistrar commandRegistrar() {
        return this.registrar;
    }

    private void registerProvidedValues() {
        this.providedValueRegistry.register(ProxyServer.class, new ProvidedValueFactory<ProxyServer>() {
            @Override
            public Class<ProxyServer> type() {
                return ProxyServer.class;
            }

            @Override
            public ProxyServer provide(CommandContext<?> context) {
                return VelocityCommandFramework.this.proxyServer;
            }
        });
        registerRuntimePluginType();
    }

    @SuppressWarnings("unchecked")
    private void registerRuntimePluginType() {
        this.providedValueRegistry.register((Class<Object>) this.plugin.getClass(), new ProvidedValueFactory<Object>() {
            @Override
            public Class<Object> type() {
                return (Class<Object>) VelocityCommandFramework.this.plugin.getClass();
            }

            @Override
            public Object provide(CommandContext<?> context) {
                return VelocityCommandFramework.this.plugin;
            }
        });
    }

    public static final class Builder {

        private final ProxyServer proxyServer;
        private final Object plugin;
        private Executor asyncExecutor;
        private ExceptionMessageResolver<CommandSource> messageResolver;

        private Builder(ProxyServer proxyServer, Object plugin) {
            this.proxyServer = proxyServer;
            this.plugin = plugin;
        }

        public Builder asyncExecutor(Executor asyncExecutor) {
            this.asyncExecutor = asyncExecutor;
            return this;
        }

        public Builder messageResolver(ExceptionMessageResolver<CommandSource> messageResolver) {
            this.messageResolver = messageResolver;
            return this;
        }

        public VelocityCommandFramework build() {
            return new VelocityCommandFramework(this.proxyServer, this.plugin, this.asyncExecutor, this.messageResolver);
        }
    }
}
