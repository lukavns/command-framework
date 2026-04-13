package me.lukavns.commandframework.examples.bungee;

import me.lukavns.commandframework.api.annotation.Command;
import me.lukavns.commandframework.api.annotation.OptionalArg;
import me.lukavns.commandframework.api.annotation.Remaining;
import me.lukavns.commandframework.api.annotation.SubCommand;
import me.lukavns.commandframework.api.annotation.Suggest;
import me.lukavns.commandframework.api.context.Context;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

@Command(
    name = "framework",
    aliases = {"cf"},
    description = "Example commands for the Bungee module."
)
public final class BungeeExampleCommands {

    private final Plugin plugin;

    public BungeeExampleCommands(Plugin plugin) {
        this.plugin = plugin;
    }

    public void root(Context<CommandSender> context) {
        context.sendMessage("Try /framework greet <player> [message] or /framework count");
    }

    @SubCommand(
        name = "greet",
        usage = "framework greet <player> [message]",
        aliases = {"hello"},
        description = "Greets an online Bungee player."
    )
    public void greet(
        Context<CommandSender> context,
        @Suggest(provider = "online-players") ProxiedPlayer target,
        @OptionalArg({"Hello from Bungee"}) @Remaining String message
    ) {
        target.sendMessage(new TextComponent(message + " from " + context.getSender().getName()));
        context.sendMessage("Sent a greeting to " + target.getName());
    }

    @SubCommand(
        name = "count",
        permission = "commandframework.example.count"
    )
    public void count(Context<CommandSender> context, ProxyServer proxyServer, Plugin plugin) {
        context.sendMessage(
            "There are " + proxyServer.getOnlineCount() + " players online on "
                + plugin.getDescription().getName() + " via " + this.plugin.getProxy().getName()
        );
    }
}
