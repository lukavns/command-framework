package me.lukavns.commandframework.bukkit.command;

import java.util.List;
import me.lukavns.commandframework.api.command.CommandResult;
import me.lukavns.commandframework.bukkit.suggestion.BukkitSuggestionAdapter;
import me.lukavns.commandframework.core.command.RootCommandMetadata;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public final class FrameworkBukkitCommand extends Command {

    private final BukkitSuggestionAdapter adapter;

    public FrameworkBukkitCommand(RootCommandMetadata metadata, BukkitSuggestionAdapter adapter) {
        super(metadata.name(), metadata.description(), metadata.usage(), metadata.aliases());
        this.adapter = adapter;
        if (!metadata.permission().isEmpty()) {
            setPermission(metadata.permission());
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        CommandResult result = this.adapter.dispatch(sender, commandLabel, args);
        return result.isHandled() && result.isSuccess();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return this.adapter.suggest(sender, alias, args);
    }
}
