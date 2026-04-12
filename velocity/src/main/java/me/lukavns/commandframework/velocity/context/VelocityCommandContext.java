package me.lukavns.commandframework.velocity.context;

import java.util.List;
import java.util.Map;
import me.lukavns.commandframework.core.dispatch.BaseCommandContext;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Velocity-specific command context backed by a {@link CommandSource}.
 */
public final class VelocityCommandContext extends BaseCommandContext<CommandSource> {

    public VelocityCommandContext(
        CommandSource sender,
        String label,
        List<String> rawArguments,
        Map<Class<?>, Object> providedValues
    ) {
        super(sender, label, rawArguments, providedValues);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permission == null || permission.isEmpty() || sender().hasPermission(permission);
    }

    @Override
    public void sendMessage(String message) {
        sender().sendMessage(LegacyComponentSerializer.legacySection().deserialize(message));
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }
}
