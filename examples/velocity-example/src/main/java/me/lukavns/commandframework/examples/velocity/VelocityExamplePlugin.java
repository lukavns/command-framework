package me.lukavns.commandframework.examples.velocity;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import me.lukavns.commandframework.velocity.bootstrap.VelocityCommandFramework;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

@Singleton
@Plugin(
    id = "commandframework-example",
    name = "Command Framework Example",
    version = "0.1.0-SNAPSHOT",
    description = "Example plugin for Command Framework"
)
public final class VelocityExamplePlugin {

    private final ProxyServer proxyServer;
    private VelocityCommandFramework framework;

    @Inject
    public VelocityExamplePlugin(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        this.framework = VelocityCommandFramework.builder(this.proxyServer, this).build();
        this.framework.suggestions().register("online-players", context -> {
            List<String> names = new ArrayList<String>();
            for (Player player : this.proxyServer.getAllPlayers()) {
                names.add(player.getUsername());
            }
            return names;
        });
        this.framework.register(new VelocityExampleCommands());
    }
}
