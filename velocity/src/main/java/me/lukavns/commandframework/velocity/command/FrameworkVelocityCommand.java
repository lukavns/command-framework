package me.lukavns.commandframework.velocity.command;

import java.util.List;
import me.lukavns.commandframework.velocity.suggestion.VelocitySuggestionAdapter;
import com.velocitypowered.api.command.SimpleCommand;

public final class FrameworkVelocityCommand implements SimpleCommand {

    private final VelocitySuggestionAdapter adapter;
    private final String permission;

    public FrameworkVelocityCommand(VelocitySuggestionAdapter adapter, String permission) {
        this.adapter = adapter;
        this.permission = permission;
    }

    @Override
    public void execute(Invocation invocation) {
        this.adapter.dispatch(invocation.source(), invocation.alias(), invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return this.adapter.suggest(invocation.source(), invocation.alias(), invocation.arguments());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return this.permission == null || this.permission.isEmpty() || invocation.source().hasPermission(this.permission);
    }
}
