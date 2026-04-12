package me.lukavns.commandframework.bungee.registration;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import me.lukavns.commandframework.bungee.command.FrameworkBungeeCommand;
import me.lukavns.commandframework.bungee.suggestion.BungeeSuggestionAdapter;
import me.lukavns.commandframework.core.command.RootCommandMetadata;
import me.lukavns.commandframework.core.registration.CommandRegistry;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeCommandRegistrar {

    private final Plugin plugin;
    private final CommandRegistry registry;
    private final BungeeSuggestionAdapter adapter;
    private final Set<String> registeredRoots = new LinkedHashSet<String>();

    public BungeeCommandRegistrar(Plugin plugin, CommandRegistry registry, BungeeSuggestionAdapter adapter) {
        this.plugin = plugin;
        this.registry = registry;
        this.adapter = adapter;
    }

    public void sync() {
        for (Map.Entry<String, RootCommandMetadata> entry : this.registry.roots().entrySet()) {
            if (this.registeredRoots.contains(entry.getKey())) {
                continue;
            }

            this.plugin.getProxy().getPluginManager().registerCommand(
                this.plugin,
                new FrameworkBungeeCommand(entry.getValue(), this.adapter)
            );
            this.registeredRoots.add(entry.getKey());
        }
    }
}
