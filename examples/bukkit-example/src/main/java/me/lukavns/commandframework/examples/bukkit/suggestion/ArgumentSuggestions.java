package me.lukavns.commandframework.examples.bukkit.suggestion;

import java.util.ArrayList;
import java.util.List;
import me.lukavns.commandframework.bukkit.bootstrap.BukkitCommandFramework;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ArgumentSuggestions {

    private ArgumentSuggestions() {}

    public static void register(BukkitCommandFramework commandFramework) {
        commandFramework.argumentSuggestions().register("online-players", context -> {
            List<String> names = new ArrayList<String>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            return names;
        });
    }
}
