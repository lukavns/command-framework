package me.lukavns.commandframework.examples.bungee.suggestion;

import java.util.ArrayList;
import java.util.List;
import me.lukavns.commandframework.bungee.bootstrap.BungeeCommandFramework;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class ArgumentSuggestions {

    private ArgumentSuggestions() {}

    public static void register(BungeeCommandFramework commandFramework) {
        commandFramework.argumentSuggestions().register("online-players", context -> {
            List<String> names = new ArrayList<String>();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                names.add(player.getName());
            }
            return names;
        });
    }
}
