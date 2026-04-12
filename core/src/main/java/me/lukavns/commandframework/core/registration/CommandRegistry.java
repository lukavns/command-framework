package me.lukavns.commandframework.core.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import me.lukavns.commandframework.api.command.CommandDefinition;
import me.lukavns.commandframework.api.exception.CommandRegistrationException;
import me.lukavns.commandframework.core.command.CommandMatch;
import me.lukavns.commandframework.core.command.RootCommandMetadata;

public final class CommandRegistry {

    private final Map<String, String> labelIndex = new LinkedHashMap<String, String>();
    private final Map<String, List<CommandDefinition>> definitionsByRoot = new LinkedHashMap<String, List<CommandDefinition>>();

    public void register(Collection<CommandDefinition> definitions) {
        for (CommandDefinition definition : definitions) {
            String rootKey = normalize(definition.name());
            List<CommandDefinition> registered = this.definitionsByRoot.get(rootKey);
            if (registered == null) {
                registered = new ArrayList<CommandDefinition>();
                this.definitionsByRoot.put(rootKey, registered);
            }

            for (CommandDefinition existing : registered) {
                if (existing.fullPath().equalsIgnoreCase(definition.fullPath())) {
                    throw new CommandRegistrationException("Duplicate command path registered: " + definition.fullPath());
                }
            }

            registerLabel(definition.name(), rootKey);
            for (String alias : definition.aliases()) {
                registerLabel(alias, rootKey);
            }

            registered.add(definition);
        }
    }

    public Collection<CommandDefinition> definitions() {
        List<CommandDefinition> definitions = new ArrayList<CommandDefinition>();
        for (List<CommandDefinition> entries : this.definitionsByRoot.values()) {
            definitions.addAll(entries);
        }
        return Collections.unmodifiableList(definitions);
    }

    public List<CommandDefinition> definitionsForLabel(String label) {
        String rootKey = this.labelIndex.get(normalize(label));
        if (rootKey == null) {
            return Collections.emptyList();
        }
        List<CommandDefinition> definitions = this.definitionsByRoot.get(rootKey);
        if (definitions == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(definitions);
    }

    public Map<String, RootCommandMetadata> roots() {
        Map<String, RootCommandMetadata> roots = new LinkedHashMap<String, RootCommandMetadata>();
        for (List<CommandDefinition> definitions : this.definitionsByRoot.values()) {
            if (definitions.isEmpty()) {
                continue;
            }
            CommandDefinition first = definitions.get(0);
            RootCommandMetadata metadata = new RootCommandMetadata(first.name());
            for (CommandDefinition definition : definitions) {
                metadata.absorb(definition);
            }
            roots.put(first.name(), metadata);
        }
        return Collections.unmodifiableMap(roots);
    }

    public CommandMatch match(String label, List<String> arguments) {
        List<CommandDefinition> definitions = definitionsForLabel(label);
        CommandMatch best = null;

        for (CommandDefinition definition : definitions) {
            List<String> subcommandPath = definition.subcommandPath();
            if (arguments.size() < subcommandPath.size()) {
                continue;
            }

            boolean matched = true;
            for (int index = 0; index < subcommandPath.size(); index++) {
                if (!subcommandPath.get(index).equalsIgnoreCase(arguments.get(index))) {
                    matched = false;
                    break;
                }
            }

            if (!matched) {
                continue;
            }

            if (best == null || definition.subcommandPath().size() > best.definition().subcommandPath().size()) {
                best = new CommandMatch(definition, subcommandPath.size());
            }
        }

        return best;
    }

    private void registerLabel(String label, String rootKey) {
        String normalized = normalize(label);
        String existing = this.labelIndex.get(normalized);
        if (existing != null && !existing.equals(rootKey)) {
            throw new CommandRegistrationException("Command label '" + label + "' is already owned by " + existing);
        }
        this.labelIndex.put(normalized, rootKey);
    }

    private static String normalize(String input) {
        return input.toLowerCase(Locale.ROOT);
    }
}
