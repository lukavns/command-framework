package me.lukavns.commandframework.examples.velocity;

import me.lukavns.commandframework.api.annotation.Command;
import me.lukavns.commandframework.api.annotation.OptionalArg;
import me.lukavns.commandframework.api.annotation.Remaining;
import me.lukavns.commandframework.api.annotation.SubCommand;
import me.lukavns.commandframework.api.annotation.Suggest;
import me.lukavns.commandframework.api.context.Context;
import net.kyori.adventure.text.Component;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

@Command(
    name = "framework",
    aliases = {"cf"},
    description = "Example commands for the Velocity module."
)
public final class VelocityExampleCommands {

    public void root(Context<CommandSource> context) {
        context.sendMessage("Try /framework greet <player> [message] or /framework count <amount>");
    }

    @SubCommand(
        name = "greet",
        usage = "framework greet <player> [message]",
        aliases = {"hello"},
        description = "Greets an online Velocity player."
    )
    public void greet(
        Context<CommandSource> context,
        @Suggest(provider = "online-players") Player target,
        @OptionalArg({"Hello from Velocity"}) @Remaining String message
    ) {
        target.sendMessage(Component.text(message));
        context.sendMessage("Sent a greeting to " + target.getUsername());
    }

    @SubCommand(
        name = "count",
        description = "Counts the number of online players."
    )
    public boolean count(Context<CommandSource> context, int amount, ProxyServer proxyServer) {
        context.sendMessage("There are " + proxyServer.getPlayerCount() + " players online. Count arg: " + amount);
        return true;
    }
}
