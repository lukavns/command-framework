package me.lukavns.commandframework.bukkit.registration;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import me.lukavns.commandframework.bukkit.command.FrameworkBukkitCommand;
import me.lukavns.commandframework.bukkit.suggestion.BukkitSuggestionAdapter;
import me.lukavns.commandframework.core.command.RootCommandMetadata;
import me.lukavns.commandframework.core.registration.CommandRegistry;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

public final class BukkitCommandRegistrar {

    private final Plugin plugin;
    private final CommandRegistry registry;
    private final BukkitSuggestionAdapter adapter;
    private final CommandMap commandMap;
    private final Map<String, FrameworkBukkitCommand> commands = new LinkedHashMap<String, FrameworkBukkitCommand>();

    public BukkitCommandRegistrar(Plugin plugin, CommandRegistry registry, BukkitSuggestionAdapter adapter) {
        this.plugin = plugin;
        this.registry = registry;
        this.adapter = adapter;
        this.commandMap = resolveCommandMap(plugin);
    }

    public void sync() {
        for (Map.Entry<String, RootCommandMetadata> entry : this.registry.roots().entrySet()) {
            if (this.commands.containsKey(entry.getKey())) {
                continue;
            }

            FrameworkBukkitCommand command = new FrameworkBukkitCommand(entry.getValue(), this.adapter);
            this.commandMap.register(this.plugin.getName(), command);
            this.commands.put(entry.getKey(), command);
        }
    }

    private static CommandMap resolveCommandMap(Plugin plugin) {
        try {
            Method method = plugin.getServer().getClass().getMethod("getCommandMap");
            return (CommandMap) method.invoke(plugin.getServer());
        } catch (Exception exception) {
            throw new IllegalStateException("Could not access the Bukkit CommandMap", exception);
        }
    }
}
