package me.lukavns.commandframework.bungee.command;

import me.lukavns.commandframework.bungee.suggestion.BungeeSuggestionAdapter;
import me.lukavns.commandframework.core.command.RootCommandMetadata;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public final class FrameworkBungeeCommand extends Command implements TabExecutor {

    private final BungeeSuggestionAdapter adapter;

    public FrameworkBungeeCommand(RootCommandMetadata metadata, BungeeSuggestionAdapter adapter) {
        super(metadata.name(), metadata.permission().isEmpty() ? null : metadata.permission(), metadata.aliases().toArray(new String[0]));
        this.adapter = adapter;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.adapter.dispatch(sender, getName(), args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return this.adapter.suggest(sender, getName(), args);
    }
}
