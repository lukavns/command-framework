package me.lukavns.commandframework.bukkit.resolve;

import java.util.UUID;
import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.exception.ArgumentParseException;
import me.lukavns.commandframework.api.resolve.ArgumentResolver;
import me.lukavns.commandframework.core.resolve.DefaultArgumentResolverRegistry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class BukkitArgumentResolvers {

    private BukkitArgumentResolvers() {
    }

    public static void registerDefaults(DefaultArgumentResolverRegistry registry) {
        registry.register(Player.class, new ArgumentResolver<Player>() {
            @Override
            public Player resolve(CommandContext<?> ignoredContext, ParameterDefinition ignoredParameter, String input) {
                Player player = Bukkit.getPlayerExact(input);
                if (player == null) {
                    player = Bukkit.getPlayer(input);
                }
                if (player == null) {
                    throw new ArgumentParseException("Unknown online player: " + input);
                }
                return player;
            }
        });

        registry.register(OfflinePlayer.class, new ArgumentResolver<OfflinePlayer>() {
            @Override
            public OfflinePlayer resolve(CommandContext<?> ignoredContext, ParameterDefinition ignoredParameter, String input) {
                OfflinePlayer offlinePlayer = resolveOfflinePlayer(input);
                if (offlinePlayer == null) {
                    throw new ArgumentParseException("Unknown player: " + input);
                }
                return offlinePlayer;
            }
        });
    }

    private static OfflinePlayer resolveOfflinePlayer(String input) {
        Player onlinePlayer = Bukkit.getPlayerExact(input);
        if (onlinePlayer != null) {
            return onlinePlayer;
        }

        try {
            UUID uuid = UUID.fromString(input);
            return Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException ignored) {
        }

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            String name = offlinePlayer.getName();
            if (name != null && name.equalsIgnoreCase(input)) {
                return offlinePlayer;
            }
        }

        return null;
    }
}
