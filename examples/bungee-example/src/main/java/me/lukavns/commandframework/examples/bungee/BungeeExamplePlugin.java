package me.lukavns.commandframework.examples.bungee;

import me.lukavns.commandframework.bungee.bootstrap.BungeeCommandFramework;
import me.lukavns.commandframework.examples.bungee.message.ExceptionMessages;
import me.lukavns.commandframework.examples.bungee.suggestion.ArgumentSuggestions;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeExamplePlugin extends Plugin {

    private BungeeCommandFramework framework;

    @Override
    public void onEnable() {
        this.framework = BungeeCommandFramework.builder(this).build();
        
        ExceptionMessages.register(this.framework);
        ArgumentSuggestions.register(this.framework);

        this.framework.registerCommands(
            new BungeeExampleCommands(this)
        );
    }
}
