package me.lukavns.commandframework.examples.bukkit;

import java.util.ArrayList;
import java.util.List;
import me.lukavns.commandframework.bukkit.bootstrap.BukkitCommandFramework;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitExamplePlugin extends JavaPlugin {

    private BukkitCommandFramework framework;

    @Override
    public void onEnable() {
        this.framework = BukkitCommandFramework.builder(this).build();
        this.framework.suggestions().register("online-players", context -> {
            List<String> names = new ArrayList<String>();
            for (Player player : getServer().getOnlinePlayers()) {
                names.add(player.getName());
            }
            return names;
        });

        this.framework.register(new BukkitExampleCommands(this));
    }
}
