package me.lukavns.commandframework.examples.bungee;

import java.util.ArrayList;
import java.util.List;
import me.lukavns.commandframework.bungee.bootstrap.BungeeCommandFramework;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeExamplePlugin extends Plugin {

    private BungeeCommandFramework framework;

    @Override
    public void onEnable() {
        this.framework = BungeeCommandFramework.builder(this).build();
        this.framework.suggestions().register("online-players", context -> {
            List<String> names = new ArrayList<String>();
            for (ProxiedPlayer player : getProxy().getPlayers()) {
                names.add(player.getName());
            }
            return names;
        });

        this.framework.register(new BungeeExampleCommands(this));
    }
}
