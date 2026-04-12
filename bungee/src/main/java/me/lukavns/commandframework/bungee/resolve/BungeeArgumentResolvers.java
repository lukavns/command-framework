package me.lukavns.commandframework.bungee.resolve;

import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.exception.ArgumentParseException;
import me.lukavns.commandframework.api.resolve.ArgumentResolver;
import me.lukavns.commandframework.core.resolve.DefaultArgumentResolverRegistry;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class BungeeArgumentResolvers {

    private BungeeArgumentResolvers() {
    }

    public static void registerDefaults(DefaultArgumentResolverRegistry registry, final ProxyServer proxyServer) {
        registry.register(ProxiedPlayer.class, new ArgumentResolver<ProxiedPlayer>() {
            @Override
            public ProxiedPlayer resolve(CommandContext<?> context, ParameterDefinition parameter, String input) {
                ProxiedPlayer player = proxyServer.getPlayer(input);
                if (player == null) {
                    throw new ArgumentParseException("Unknown online player: " + input);
                }
                return player;
            }
        });
    }
}
