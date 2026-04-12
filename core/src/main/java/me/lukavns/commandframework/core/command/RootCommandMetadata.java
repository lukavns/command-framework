package me.lukavns.commandframework.core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import me.lukavns.commandframework.api.command.CommandDefinition;

public final class RootCommandMetadata {

    private final String name;
    private final Set<String> aliases;
    private String description;
    private String usage;
    private String permission;
    private boolean permissionMixed;

    public RootCommandMetadata(String name) {
        this.name = name;
        this.aliases = new LinkedHashSet<String>();
    }

    public void absorb(CommandDefinition definition) {
        this.aliases.addAll(definition.aliases());
        if (this.description == null || this.description.isEmpty()) {
            this.description = definition.description();
        }
        if (this.usage == null || this.usage.isEmpty()) {
            this.usage = definition.usage();
        }

        String definitionPermission = definition.permission();
        if (!this.permissionMixed) {
            if (this.permission == null) {
                this.permission = definitionPermission;
            } else if (!this.permission.equals(definitionPermission)) {
                this.permission = "";
                this.permissionMixed = true;
            }
        }
    }

    public String name() {
        return this.name;
    }

    public List<String> aliases() {
        return Collections.unmodifiableList(new ArrayList<String>(this.aliases));
    }

    public String description() {
        return this.description == null ? "" : this.description;
    }

    public String usage() {
        return this.usage == null ? "" : this.usage;
    }

    public String permission() {
        return this.permission == null ? "" : this.permission;
    }
}
