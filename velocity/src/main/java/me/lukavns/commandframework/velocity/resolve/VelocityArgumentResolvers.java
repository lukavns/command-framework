package me.lukavns.commandframework.velocity.resolve;

import java.util.Optional;
import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.exception.ArgumentParseException;
import me.lukavns.commandframework.api.resolve.ArgumentResolver;
import me.lukavns.commandframework.core.resolve.DefaultArgumentResolverRegistry;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

public final class VelocityArgumentResolvers {

    private VelocityArgumentResolvers() {
    }

    public static void registerDefaults(DefaultArgumentResolverRegistry registry, final ProxyServer proxyServer) {
        registry.register(Player.class, new ArgumentResolver<Player>() {
            @Override
            public Player resolve(CommandContext<?> context, ParameterDefinition parameter, String input) {
                Optional<Player> player = proxyServer.getPlayer(input);
                if (!player.isPresent()) {
                    throw new ArgumentParseException("Unknown online player: " + input);
                }
                return player.get();
            }
        });
    }
}
