package me.lukavns.commandframework.velocity.registration;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import me.lukavns.commandframework.core.command.RootCommandMetadata;
import me.lukavns.commandframework.core.registration.CommandRegistry;
import me.lukavns.commandframework.velocity.command.FrameworkVelocityCommand;
import me.lukavns.commandframework.velocity.suggestion.VelocitySuggestionAdapter;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.proxy.ProxyServer;

public final class VelocityCommandRegistrar {

    private final ProxyServer proxyServer;
    private final Object plugin;
    private final CommandRegistry registry;
    private final VelocitySuggestionAdapter adapter;
    private final Set<String> registeredRoots = new LinkedHashSet<String>();

    public VelocityCommandRegistrar(
        ProxyServer proxyServer,
        Object plugin,
        CommandRegistry registry,
        VelocitySuggestionAdapter adapter
    ) {
        this.proxyServer = proxyServer;
        this.plugin = plugin;
        this.registry = registry;
        this.adapter = adapter;
    }

    public void sync() {
        CommandManager commandManager = this.proxyServer.getCommandManager();
        for (Map.Entry<String, RootCommandMetadata> entry : this.registry.roots().entrySet()) {
            if (this.registeredRoots.contains(entry.getKey())) {
                continue;
            }

            RootCommandMetadata metadata = entry.getValue();
            CommandMeta commandMeta = commandManager.metaBuilder(metadata.name())
                .aliases(metadata.aliases().toArray(new String[0]))
                .plugin(this.plugin)
                .build();

            commandManager.register(commandMeta, new FrameworkVelocityCommand(this.adapter, metadata.permission()));
            this.registeredRoots.add(entry.getKey());
        }
    }
}
