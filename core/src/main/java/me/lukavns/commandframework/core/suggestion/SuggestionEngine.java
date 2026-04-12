package me.lukavns.commandframework.core.suggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import me.lukavns.commandframework.api.command.CommandDefinition;
import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.command.ParameterSourceKind;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.suggestion.SuggestionContext;
import me.lukavns.commandframework.api.suggestion.SuggestionDescriptor;
import me.lukavns.commandframework.api.suggestion.SuggestionProvider;

public final class SuggestionEngine<S> {

    private final DefaultSuggestionRegistry<S> suggestionRegistry;

    public SuggestionEngine(DefaultSuggestionRegistry<S> suggestionRegistry) {
        this.suggestionRegistry = suggestionRegistry;
    }

    public List<String> suggest(CommandContext<S> context, List<CommandDefinition> definitions) {
        if (definitions.isEmpty()) {
            return Collections.emptyList();
        }

        List<CommandDefinition> visibleDefinitions = visibleDefinitions(context, definitions);
        if (visibleDefinitions.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> arguments = context.rawArguments();
        String currentInput = arguments.isEmpty() ? "" : arguments.get(arguments.size() - 1);

        Set<String> subcommandSuggestions = suggestSubcommands(arguments, currentInput, visibleDefinitions);
        if (!subcommandSuggestions.isEmpty()) {
            return new ArrayList<String>(subcommandSuggestions);
        }

        CommandDefinition selected = selectDefinition(arguments, visibleDefinitions);
        if (selected == null) {
            return Collections.emptyList();
        }

        List<ParameterDefinition> argumentParameters = new ArrayList<ParameterDefinition>();
        for (ParameterDefinition parameter : selected.parameters()) {
            if (parameter.sourceKind() == ParameterSourceKind.ARGUMENT) {
                argumentParameters.add(parameter);
            }
        }

        int parameterIndex = Math.max(0, arguments.size() - selected.subcommandPath().size() - 1);
        if (argumentParameters.isEmpty() || parameterIndex >= argumentParameters.size()) {
            return Collections.emptyList();
        }

        ParameterDefinition parameter = argumentParameters.get(parameterIndex);
        SuggestionDescriptor descriptor = parameter.suggestionDescriptor();

        if (descriptor.hasStaticSuggestions()) {
            return filterByPrefix(descriptor.staticSuggestions(), currentInput);
        }

        if (descriptor.hasProvider()) {
            Optional<SuggestionProvider<S>> provider = this.suggestionRegistry.find(descriptor.providerName());
            if (provider.isPresent()) {
                List<String> consumed = arguments.isEmpty()
                    ? Collections.<String>emptyList()
                    : arguments.subList(0, arguments.size() - 1);
                List<String> providerSuggestions = provider.get().suggest(
                    new SuggestionContext<S>(context, selected, parameter, consumed, currentInput)
                );
                return filterByPrefix(
                    providerSuggestions == null ? Collections.<String>emptyList() : providerSuggestions,
                    currentInput
                );
            }
        }

        if (parameter.parameterType().isEnum()) {
            List<String> values = new ArrayList<String>();
            Object[] constants = parameter.parameterType().getEnumConstants();
            for (Object constant : constants) {
                values.add(((Enum<?>) constant).name().toLowerCase(Locale.ROOT));
            }
            return filterByPrefix(values, currentInput);
        }

        return Collections.emptyList();
    }

    private Set<String> suggestSubcommands(List<String> arguments, String currentInput, List<CommandDefinition> definitions) {
        Set<String> suggestions = new LinkedHashSet<String>();
        int position = arguments.isEmpty() ? 0 : arguments.size() - 1;

        for (CommandDefinition definition : definitions) {
            List<String> path = definition.subcommandPath();
            if (path.size() <= position) {
                continue;
            }

            boolean previousSegmentsMatch = true;
            for (int index = 0; index < position; index++) {
                if (arguments.size() <= index || !path.get(index).equalsIgnoreCase(arguments.get(index))) {
                    previousSegmentsMatch = false;
                    break;
                }
            }
            if (!previousSegmentsMatch) {
                continue;
            }

            String candidate = path.get(position);
            if (startsWithIgnoreCase(candidate, currentInput)) {
                suggestions.add(candidate);
            }
        }

        return suggestions;
    }

    private CommandDefinition selectDefinition(List<String> arguments, List<CommandDefinition> definitions) {
        CommandDefinition best = null;
        for (CommandDefinition definition : definitions) {
            List<String> path = definition.subcommandPath();
            if (arguments.size() < path.size()) {
                continue;
            }

            boolean matches = true;
            for (int index = 0; index < path.size(); index++) {
                if (!path.get(index).equalsIgnoreCase(arguments.get(index))) {
                    matches = false;
                    break;
                }
            }

            if (matches && (best == null || path.size() > best.subcommandPath().size())) {
                best = definition;
            }
        }
        return best;
    }

    private List<String> filterByPrefix(List<String> inputs, String currentInput) {
        List<String> values = new ArrayList<String>();
        for (String input : inputs) {
            if (input != null && startsWithIgnoreCase(input, currentInput)) {
                values.add(input);
            }
        }
        return values;
    }

    private List<CommandDefinition> visibleDefinitions(CommandContext<S> context, List<CommandDefinition> definitions) {
        List<CommandDefinition> visible = new ArrayList<CommandDefinition>();
        for (CommandDefinition definition : definitions) {
            if (isVisible(context, definition)) {
                visible.add(definition);
            }
        }
        return visible;
    }

    private boolean isVisible(CommandContext<S> context, CommandDefinition definition) {
        if (!definition.permission().isEmpty() && !context.hasPermission(definition.permission())) {
            return false;
        }

        if (definition.senderTargets().isEmpty()) {
            return true;
        }

        for (Class<?> senderTarget : definition.senderTargets()) {
            if (senderTarget.isInstance(context.sender())) {
                return true;
            }
        }

        return false;
    }

    private boolean startsWithIgnoreCase(String value, String prefix) {
        return value.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT));
    }
}
