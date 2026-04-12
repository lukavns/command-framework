package me.lukavns.commandframework.core.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import me.lukavns.commandframework.api.CommandScanner;
import me.lukavns.commandframework.api.annotation.Command;
import me.lukavns.commandframework.api.annotation.OptionalArg;
import me.lukavns.commandframework.api.annotation.Remaining;
import me.lukavns.commandframework.api.annotation.SubCommand;
import me.lukavns.commandframework.api.annotation.Suggest;
import me.lukavns.commandframework.api.command.CommandDefinition;
import me.lukavns.commandframework.api.command.CommandResult;
import me.lukavns.commandframework.api.command.CommandTarget;
import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.command.ParameterSourceKind;
import me.lukavns.commandframework.api.exception.CommandRegistrationException;
import me.lukavns.commandframework.api.exception.MissingResolverException;
import me.lukavns.commandframework.api.suggestion.SuggestionDescriptor;
import me.lukavns.commandframework.core.internal.ParameterClassifier;
import me.lukavns.commandframework.core.resolve.DefaultArgumentResolverRegistry;
import me.lukavns.commandframework.core.resolve.DefaultProvidedValueRegistry;
import me.lukavns.commandframework.core.util.CommandPathUtils;
import me.lukavns.commandframework.core.util.UsageBuilder;

public final class AnnotationCommandScanner implements CommandScanner {

    private final DefaultArgumentResolverRegistry resolverRegistry;
    private final ParameterClassifier parameterClassifier;
    private final Set<Class<?>> senderTypes;

    public AnnotationCommandScanner(
        DefaultArgumentResolverRegistry resolverRegistry,
        DefaultProvidedValueRegistry providedValueRegistry,
        Set<Class<?>> senderTypes
    ) {
        this.resolverRegistry = resolverRegistry;
        this.senderTypes = new LinkedHashSet<Class<?>>(senderTypes);
        this.parameterClassifier = new ParameterClassifier(this.senderTypes, providedValueRegistry);
    }

    @Override
    public List<CommandDefinition> scan(Object... commandHolders) {
        List<CommandDefinition> definitions = new ArrayList<CommandDefinition>();
        for (Object commandHolder : commandHolders) {
            if (commandHolder == null) {
                continue;
            }
            definitions.addAll(scanHolder(commandHolder));
        }
        return Collections.unmodifiableList(definitions);
    }

    private List<CommandDefinition> scanHolder(Object commandHolder) {
        Class<?> holderType = commandHolder.getClass();
        Command classCommand = holderType.getAnnotation(Command.class);
        List<CommandDefinition> definitions = new ArrayList<CommandDefinition>();
        List<Method> defaultRootCandidates = new ArrayList<Method>();

        for (Method method : holderType.getDeclaredMethods()) {
            if (method.isSynthetic() || method.isBridge() || Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            Command methodCommand = method.getAnnotation(Command.class);
            SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if (methodCommand != null || subCommand != null) {
                definitions.addAll(buildDefinitions(commandHolder, classCommand, methodCommand, subCommand, false, method));
                continue;
            }

            if (classCommand != null && isDefaultRootHandlerCandidate(method)) {
                defaultRootCandidates.add(method);
            }
        }

        if (defaultRootCandidates.size() > 1) {
            throw new CommandRegistrationException(
                "Multiple default command handlers were found in " + holderType.getName()
                    + ". Keep only one public root method without @SubCommand or annotate the others explicitly."
            );
        }

        if (!defaultRootCandidates.isEmpty()) {
            definitions.addAll(buildDefinitions(commandHolder, classCommand, null, null, true, defaultRootCandidates.get(0)));
        }

        return definitions;
    }

    private boolean isDefaultRootHandlerCandidate(Method method) {
        if (!Modifier.isPublic(method.getModifiers()) || !supportsReturnType(method.getReturnType())) {
            return false;
        }
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return true;
        }
        return this.parameterClassifier.classify(parameters[0].getType()) != ParameterSourceKind.ARGUMENT;
    }

    private List<CommandDefinition> buildDefinitions(
        Object instance,
        Command classCommand,
        Command methodCommand,
        SubCommand subCommand,
        boolean defaultRoot,
        Method method
    ) {
        boolean groupedCommand = methodCommand == null;
        if (groupedCommand && classCommand == null) {
            throw new CommandRegistrationException(
                "Method " + method.getDeclaringClass().getName() + "#" + method.getName()
                    + " must declare @Command or belong to a class-level @Command group"
            );
        }

        validateReturnType(method);

        List<String> rootPath = groupedCommand ? commandPath(classCommand) : commandPath(methodCommand);
        if (rootPath.isEmpty()) {
            throw new CommandRegistrationException("Command path cannot be empty for " + method.getDeclaringClass().getName() + "#" + method.getName());
        }

        List<String> relativeSubcommandPath = groupedCommand
            ? relativeSubcommandPath(rootPath, subCommand, defaultRoot)
            : Collections.<String>emptyList();

        List<String> fullPath = groupedCommand ? combine(rootPath, relativeSubcommandPath) : rootPath;
        List<ParameterDefinition> parameters = inspectParameters(method);
        List<String> rootAliases = groupedCommand ? aliasesOf(classCommand) : aliasesOf(methodCommand);
        List<Class<?>> senderTargets = resolveTargets(classCommand, methodCommand, subCommand);

        String rootName = fullPath.get(0);
        List<String> subcommandPath = fullPath.size() == 1
            ? Collections.<String>emptyList()
            : fullPath.subList(1, fullPath.size());

        String description = groupedCommand
            ? firstNonEmpty(subcommandDescription(subCommand), classCommand.description())
            : methodCommand.description();
        String permission = groupedCommand
            ? firstNonEmpty(subcommandPermission(subCommand), classCommand.permission())
            : methodCommand.permission();
        String usage = groupedCommand
            ? resolveUsage(firstNonEmpty(subcommandUsage(subCommand), classCommand.usage()), rootName, subcommandPath, parameters)
            : resolveUsage(methodCommand.usage(), rootName, subcommandPath, parameters);
        boolean async = groupedCommand ? subcommandAsync(subCommand, classCommand.async()) : methodCommand.async();

        List<CommandDefinition> definitions = new ArrayList<CommandDefinition>();
        definitions.add(createDefinition(
            instance,
            method,
            rootName,
            subcommandPath,
            rootAliases,
            senderTargets,
            parameters,
            description,
            usage,
            permission,
            async
        ));

        if (groupedCommand && !defaultRoot) {
            for (List<String> aliasPath : relativeSubcommandAliases(rootPath, subCommand)) {
                List<String> fullAliasPath = combine(rootPath, aliasPath);
                if (fullAliasPath.equals(fullPath)) {
                    continue;
                }
                List<String> aliasSubcommandPath = fullAliasPath.size() == 1
                    ? Collections.<String>emptyList()
                    : fullAliasPath.subList(1, fullAliasPath.size());
                definitions.add(createDefinition(
                    instance,
                    method,
                    fullAliasPath.get(0),
                    aliasSubcommandPath,
                    rootAliases,
                    senderTargets,
                    parameters,
                    description,
                    usage,
                    permission,
                    async
                ));
            }
        }

        return definitions;
    }

    private CommandDefinition createDefinition(
        Object instance,
        Method method,
        String rootName,
        List<String> subcommandPath,
        List<String> aliases,
        List<Class<?>> senderTargets,
        List<ParameterDefinition> parameters,
        String description,
        String usage,
        String permission,
        boolean async
    ) {
        return CommandDefinition.builder()
            .name(rootName)
            .aliases(aliases)
            .description(description)
            .usage(usage)
            .permission(permission)
            .senderTargets(senderTargets)
            .async(async)
            .subcommandPath(new ArrayList<String>(subcommandPath))
            .parameters(parameters)
            .handler(new ReflectiveCommandHandler(instance, method))
            .build();
    }

    private boolean supportsReturnType(Class<?> returnType) {
        return Void.TYPE.equals(returnType)
            || Boolean.TYPE.equals(returnType)
            || Boolean.class.equals(returnType)
            || CommandResult.class.equals(returnType);
    }

    private void validateReturnType(Method method) {
        if (supportsReturnType(method.getReturnType())) {
            return;
        }
        throw new CommandRegistrationException(
            "Unsupported return type for " + method.getDeclaringClass().getName() + "#" + method.getName() + ": "
                + method.getReturnType().getName()
        );
    }

    private List<ParameterDefinition> inspectParameters(Method method) {
        List<ParameterDefinition> definitions = new ArrayList<ParameterDefinition>();
        Parameter[] parameters = method.getParameters();

        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            Class<?> parameterType = parameter.getType();
            ParameterSourceKind sourceKind = this.parameterClassifier.classify(parameterType);

            boolean remaining = parameter.isAnnotationPresent(Remaining.class);
            boolean lastParameter = index == parameters.length - 1;
            if (remaining && (sourceKind != ParameterSourceKind.ARGUMENT || !String.class.equals(parameterType) || !lastParameter)) {
                throw new CommandRegistrationException(
                    "@Remaining can only be used on the final String argument in " + method.getName()
                );
            }

            OptionalArg optionalArg = parameter.getAnnotation(OptionalArg.class);
            boolean optional = optionalArg != null;
            String defaultValue = optionalArg != null && optionalArg.value().length > 0 ? optionalArg.value()[0] : null;
            if (optional && parameterType.isPrimitive() && defaultValue == null) {
                throw new CommandRegistrationException(
                    "Optional primitive parameters require a default value in " + method.getName() + " for " + parameter.getName()
                );
            }

            if (sourceKind == ParameterSourceKind.ARGUMENT && !this.resolverRegistry.supports(parameterType)) {
                throw new MissingResolverException(parameterType);
            }

            Suggest suggest = parameter.getAnnotation(Suggest.class);
            SuggestionDescriptor suggestionDescriptor = suggest == null
                ? SuggestionDescriptor.none()
                : new SuggestionDescriptor(suggest.provider(), Arrays.asList(suggest.value()));

            definitions.add(ParameterDefinition.builder()
                .parameterType(parameterType)
                .name(parameter.getName())
                .sourceKind(sourceKind)
                .optional(optional)
                .defaultValue(defaultValue)
                .remaining(remaining)
                .resolverBinding(parameterType)
                .suggestionDescriptor(suggestionDescriptor)
                .build());
        }

        return definitions;
    }

    private List<String> aliasesOf(Command command) {
        if (command == null || command.aliases().length == 0) {
            return Collections.emptyList();
        }
        return dedupe(Arrays.asList(command.aliases()));
    }

    private List<Class<?>> resolveTargets(Command classCommand, Command methodCommand, SubCommand subCommand) {
        if (methodCommand != null) {
            return targetClasses(methodCommand.target());
        }

        if (subCommand != null && subCommand.target() != CommandTarget.ALL) {
            return targetClasses(subCommand.target());
        }

        if (classCommand == null) {
            return Collections.emptyList();
        }

        return targetClasses(classCommand.target());
    }

    private List<Class<?>> targetClasses(CommandTarget target) {
        if (target == null || target == CommandTarget.ALL) {
            return Collections.emptyList();
        }

        List<String> candidates = new ArrayList<String>();
        if (target == CommandTarget.PLAYER) {
            candidates.add("org.bukkit.entity.Player");
            candidates.add("net.md_5.bungee.api.connection.ProxiedPlayer");
            candidates.add("com.velocitypowered.api.proxy.Player");
        } else if (target == CommandTarget.CONSOLE) {
            candidates.add("org.bukkit.command.ConsoleCommandSender");
            candidates.add("net.md_5.bungee.command.ConsoleCommandSender");
            candidates.add("com.velocitypowered.api.proxy.ConsoleCommandSource");
        }

        List<Class<?>> resolved = new ArrayList<Class<?>>();
        for (String candidate : candidates) {
            Class<?> type = loadType(candidate);
            if (type != null) {
                resolved.add(type);
            }
        }

        if (resolved.isEmpty()) {
            throw new CommandRegistrationException("Could not resolve a sender type for target " + target);
        }

        return dedupeTypes(resolved);
    }

    private Class<?> loadType(String className) {
        for (Class<?> senderType : this.senderTypes) {
            ClassLoader classLoader = senderType.getClassLoader();
            if (classLoader == null) {
                continue;
            }
            try {
                return Class.forName(className, false, classLoader);
            } catch (ClassNotFoundException ignored) {
            }
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    private List<String> commandPath(Command command) {
        return CommandPathUtils.split(commandName(command));
    }

    private String commandName(Command command) {
        if (command == null) {
            return "";
        }
        if (!command.name().trim().isEmpty()) {
            return command.name();
        }
        return command.value();
    }

    private List<String> relativeSubcommandPath(List<String> rootPath, SubCommand subCommand, boolean defaultRoot) {
        if (defaultRoot) {
            return Collections.emptyList();
        }
        return normalizeRelativePath(rootPath, subcommandName(subCommand));
    }

    private List<List<String>> relativeSubcommandAliases(List<String> rootPath, SubCommand subCommand) {
        List<List<String>> aliases = new ArrayList<List<String>>();
        Set<String> dedupe = new LinkedHashSet<String>();
        for (String alias : subcommandAliases(subCommand)) {
            List<String> path = normalizeRelativePath(rootPath, alias);
            if (path.isEmpty()) {
                continue;
            }
            if (dedupe.add(join(path))) {
                aliases.add(path);
            }
        }
        return aliases;
    }

    private List<String> normalizeRelativePath(List<String> rootPath, String input) {
        List<String> path = CommandPathUtils.split(input);
        if (path.isEmpty()) {
            return Collections.emptyList();
        }
        if (startsWith(path, rootPath)) {
            return new ArrayList<String>(path.subList(rootPath.size(), path.size()));
        }
        return path;
    }

    private boolean startsWith(List<String> input, List<String> prefix) {
        if (input.size() < prefix.size()) {
            return false;
        }
        for (int index = 0; index < prefix.size(); index++) {
            if (!input.get(index).equalsIgnoreCase(prefix.get(index))) {
                return false;
            }
        }
        return true;
    }

    private List<String> combine(List<String> left, List<String> right) {
        List<String> combined = new ArrayList<String>(left);
        combined.addAll(right);
        return combined;
    }

    private String subcommandName(SubCommand subCommand) {
        if (subCommand == null) {
            return "";
        }
        if (!subCommand.name().trim().isEmpty()) {
            return subCommand.name();
        }
        return subCommand.value();
    }

    private List<String> subcommandAliases(SubCommand subCommand) {
        if (subCommand == null || subCommand.aliases().length == 0) {
            return Collections.emptyList();
        }
        return dedupe(Arrays.asList(subCommand.aliases()));
    }

    private String subcommandDescription(SubCommand subCommand) {
        return subCommand == null ? "" : subCommand.description();
    }

    private String subcommandUsage(SubCommand subCommand) {
        return subCommand == null ? "" : subCommand.usage();
    }

    private String subcommandPermission(SubCommand subCommand) {
        return subCommand == null ? "" : subCommand.permission();
    }

    private boolean subcommandAsync(SubCommand subCommand, boolean fallback) {
        return subCommand != null && subCommand.async() || fallback;
    }

    private String resolveUsage(String declaredUsage, String rootName, List<String> subcommandPath, List<ParameterDefinition> parameters) {
        if (declaredUsage != null && !declaredUsage.isEmpty()) {
            return declaredUsage;
        }
        return UsageBuilder.build(rootName, subcommandPath, parameters);
    }

    private String firstNonEmpty(String first, String second) {
        if (first != null && !first.isEmpty()) {
            return first;
        }
        return second == null ? "" : second;
    }

    private String join(List<String> input) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < input.size(); index++) {
            if (index > 0) {
                builder.append('.');
            }
            builder.append(input.get(index).toLowerCase(Locale.ROOT));
        }
        return builder.toString();
    }

    private List<String> dedupe(List<String> input) {
        Set<String> values = new LinkedHashSet<String>(input);
        return Collections.unmodifiableList(new ArrayList<String>(values));
    }

    private List<Class<?>> dedupeTypes(List<Class<?>> input) {
        Set<Class<?>> values = new LinkedHashSet<Class<?>>(input);
        return Collections.unmodifiableList(new ArrayList<Class<?>>(values));
    }
}
