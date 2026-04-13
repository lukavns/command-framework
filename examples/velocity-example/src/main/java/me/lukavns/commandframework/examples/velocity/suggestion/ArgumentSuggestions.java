package me.lukavns.commandframework.examples.velocity.suggestion;

import java.util.ArrayList;
import java.util.List;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.lukavns.commandframework.velocity.bootstrap.VelocityCommandFramework;

public final class ArgumentSuggestions {

    private ArgumentSuggestions() {}

    public static void register(VelocityCommandFramework commandFramework, ProxyServer proxyServer) {
        commandFramework.argumentSuggestions().register("online-players", context -> {
            List<String> names = new ArrayList<String>();
            for (Player player : proxyServer.getAllPlayers()) {
                names.add(player.getUsername());
            }
            return names;
        });
    }
}
