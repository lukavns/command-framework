package me.lukavns.commandframework.core.dispatch;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import me.lukavns.commandframework.api.CommandDispatcher;
import me.lukavns.commandframework.api.command.CommandDefinition;
import me.lukavns.commandframework.api.command.CommandResult;
import me.lukavns.commandframework.api.context.CommandContext;
import me.lukavns.commandframework.api.exception.CommandFrameworkException;
import me.lukavns.commandframework.api.exception.CommandInvocationException;
import me.lukavns.commandframework.api.exception.InvalidCommandSenderException;
import me.lukavns.commandframework.api.exception.PermissionDeniedException;
import me.lukavns.commandframework.core.command.CommandMatch;
import me.lukavns.commandframework.core.parse.ArgumentParser;
import me.lukavns.commandframework.core.parse.ParsedInvocation;
import me.lukavns.commandframework.core.registration.CommandRegistry;
import me.lukavns.commandframework.core.suggestion.SuggestionEngine;

public final class DefaultCommandDispatcher<S> implements CommandDispatcher<S> {

    private final CommandRegistry registry;
    private final ArgumentParser argumentParser;
    private final SuggestionEngine<S> suggestionEngine;
    private Executor asyncExecutor;

    public DefaultCommandDispatcher(
        CommandRegistry registry,
        ArgumentParser argumentParser,
        SuggestionEngine<S> suggestionEngine
    ) {
        this.registry = registry;
        this.argumentParser = argumentParser;
        this.suggestionEngine = suggestionEngine;
    }

    public void asyncExecutor(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public void register(Collection<CommandDefinition> definitions) {
        this.registry.register(definitions);
    }

    @Override
    public Collection<CommandDefinition> definitions() {
        return this.registry.definitions();
    }

    @Override
    public CommandResult dispatch(CommandContext<S> context) {
        CommandMatch match = this.registry.match(context.label(), context.rawArguments());
        if (match == null) {
            return CommandResult.noMatch();
        }

        bindMatchedCommand(context, match.definition());
        validate(context, match.definition());
        ParsedInvocation parsed = this.argumentParser.parse(context, match.definition(), match.consumedPathSegments());
        List<String> commandArguments = context.rawArguments().subList(match.consumedPathSegments(), context.rawArguments().size());
        ResolvedCommandContext<S> resolvedContext = new ResolvedCommandContext<S>(
            context,
            match.definition(),
            commandArguments,
            parsed.values()
        );
        Object[] invocationArguments = this.argumentParser.resolveContextSentinel(parsed.arguments(), resolvedContext);

        if (match.definition().async()) {
            if (this.asyncExecutor == null) {
                throw new CommandInvocationException(
                    "Command '" + match.definition().fullPath() + "' is marked async but no async executor is configured"
                );
            }

            try {
                this.asyncExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        invoke(match.definition(), resolvedContext, invocationArguments);
                    }
                });
            } catch (RuntimeException exception) {
                throw new CommandInvocationException(
                    "Could not schedule async execution for " + match.definition().fullPath(),
                    exception
                );
            }
            return CommandResult.success();
        }

        return invoke(match.definition(), resolvedContext, invocationArguments);
    }

    @Override
    public List<String> suggest(CommandContext<S> context) {
        List<CommandDefinition> definitions = this.registry.definitionsForLabel(context.label());
        if (definitions.isEmpty()) {
            return Collections.emptyList();
        }
        return this.suggestionEngine.suggest(context, definitions);
    }

    public CommandRegistry registry() {
        return this.registry;
    }

    private void validate(CommandContext<S> context, CommandDefinition definition) {
        if (!definition.permission().isEmpty() && !context.hasPermission(definition.permission())) {
            throw new PermissionDeniedException(definition.permission());
        }

        if (!definition.senderTargets().isEmpty()) {
            boolean supported = false;
            for (Class<?> senderTarget : definition.senderTargets()) {
                if (senderTarget.isInstance(context.sender())) {
                    supported = true;
                    break;
                }
            }
            if (!supported) {
                throw new InvalidCommandSenderException(definition.senderTargets());
            }
        }
    }

    private CommandResult invoke(CommandDefinition definition, CommandContext<S> context, Object[] arguments) {
        try {
            return CommandResult.fromHandlerReturn(definition.handler().invoke(context, arguments));
        } catch (CommandFrameworkException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CommandInvocationException(
                "Failed to invoke " + definition.handler().declaringType() + "#" + definition.handler().methodName(),
                exception
            );
        }
    }

    private void bindMatchedCommand(CommandContext<S> context, CommandDefinition definition) {
        if (context instanceof BaseCommandContext) {
            ((BaseCommandContext<S>) context).bindCommand(definition);
        }
    }
}
