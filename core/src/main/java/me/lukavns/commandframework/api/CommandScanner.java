package me.lukavns.commandframework.api;

import java.util.List;
import me.lukavns.commandframework.api.command.CommandDefinition;

public interface CommandScanner {

    List<CommandDefinition> scan(Object... commandHolders);
}
