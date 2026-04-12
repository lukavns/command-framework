package me.lukavns.commandframework.examples.bukkit;

import me.lukavns.commandframework.api.annotation.Command;
import me.lukavns.commandframework.api.annotation.OptionalArg;
import me.lukavns.commandframework.api.annotation.Remaining;
import me.lukavns.commandframework.api.annotation.SubCommand;
import me.lukavns.commandframework.api.annotation.Suggest;
import me.lukavns.commandframework.api.context.Context;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Command(
    name = "framework",
    aliases = {"cf"},
    description = "Example commands for the Bukkit module."
)
public final class BukkitExampleCommands {

    private final Plugin plugin;

    public BukkitExampleCommands(Plugin plugin) {
        this.plugin = plugin;
    }

    public void root(Context<CommandSender> context) {
        context.sendMessage("Try /framework greet <player> [message] or /framework reload");
    }

    @SubCommand(
        name = "greet",
        aliases = {"hello"},
        description = "Greets an online player with an optional custom message."
    )
    public void greet(
        Context<CommandSender> context,
        @Suggest(provider = "online-players") Player target,
        @OptionalArg({"Hello from Bukkit"}) @Remaining String message
    ) {
        target.sendMessage(message + " from " + context.getSender().getName());
        context.sendMessage("Sent a greeting to " + target.getName());
    }

    @SubCommand(
        name = "reload",
        permission = "commandframework.example.reload"
    )
    public void reload(Context<CommandSender> context, Plugin plugin) {
        context.sendMessage("Pretending to reload " + plugin.getName() + " on " + this.plugin.getServer().getName());
    }
}
