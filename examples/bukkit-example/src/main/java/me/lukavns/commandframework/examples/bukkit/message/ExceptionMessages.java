package me.lukavns.commandframework.examples.bukkit.message;

import me.lukavns.commandframework.api.exception.MessageType;
import me.lukavns.commandframework.bukkit.bootstrap.BukkitCommandFramework;

public final class ExceptionMessages {
    private ExceptionMessages() {}

    public static void register(BukkitCommandFramework commandFramework) {
        commandFramework.exceptionMessages()
            .setMessage(MessageType.INCORRECT_USAGE, "§cIncorrect usage, try: /{usage}")
            .setMessage(MessageType.MISSING_PERMISSION, "§cYou do not have permission to use this command.")
            .setMessage(MessageType.INCORRECT_TARGET, "§cThis command can only be used by {target}.")
            .setMessage(MessageType.INTERNAL_ERROR, "§cInternal server error, please contact an administrator.");
    }
}
