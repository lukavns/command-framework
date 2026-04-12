package me.lukavns.commandframework.api;

import java.util.Collection;
import java.util.List;
import me.lukavns.commandframework.api.command.CommandDefinition;
import me.lukavns.commandframework.api.command.CommandResult;
import me.lukavns.commandframework.api.context.CommandContext;

public interface CommandDispatcher<S> {

    void register(Collection<CommandDefinition> definitions);

    Collection<CommandDefinition> definitions();

    CommandResult dispatch(CommandContext<S> context);

    List<String> suggest(CommandContext<S> context);
}
