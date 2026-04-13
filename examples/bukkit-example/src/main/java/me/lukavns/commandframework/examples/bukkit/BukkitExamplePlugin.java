package me.lukavns.commandframework.examples.bukkit;

import me.lukavns.commandframework.bukkit.bootstrap.BukkitCommandFramework;
import me.lukavns.commandframework.examples.bukkit.message.ExceptionMessages;
import me.lukavns.commandframework.examples.bukkit.suggestion.ArgumentSuggestions;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitExamplePlugin extends JavaPlugin {

    private BukkitCommandFramework commandFramework;

    @Override
    public void onEnable() {
        this.commandFramework = BukkitCommandFramework.builder(this).build();

        ExceptionMessages.register(this.commandFramework);
        ArgumentSuggestions.register(this.commandFramework);
        
        this.commandFramework.registerCommands(
            new BukkitExampleCommands(this)
        );
    }
}
