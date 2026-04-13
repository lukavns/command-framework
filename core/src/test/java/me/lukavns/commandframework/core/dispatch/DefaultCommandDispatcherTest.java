package me.lukavns.commandframework.core.dispatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import me.lukavns.commandframework.api.command.CommandDefinition;
import me.lukavns.commandframework.api.command.CommandHandler;
import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.command.ParameterSourceKind;
import me.lukavns.commandframework.api.exception.ArgumentParseException;
import me.lukavns.commandframework.core.parse.ArgumentParser;
import me.lukavns.commandframework.core.registration.CommandRegistry;
import me.lukavns.commandframework.core.resolve.CoreArgumentResolvers;
import me.lukavns.commandframework.core.resolve.DefaultArgumentResolverRegistry;
import me.lukavns.commandframework.core.resolve.DefaultProvidedValueRegistry;
import me.lukavns.commandframework.core.suggestion.DefaultSuggestionRegistry;
import me.lukavns.commandframework.core.suggestion.SuggestionEngine;
import org.junit.jupiter.api.Test;

class DefaultCommandDispatcherTest {

    @Test
    void bindsMatchedCommandBeforeArgumentParsingFails() {
        DefaultArgumentResolverRegistry resolverRegistry = new DefaultArgumentResolverRegistry();
        DefaultProvidedValueRegistry providedValueRegistry = new DefaultProvidedValueRegistry();
        CoreArgumentResolvers.registerDefaults(resolverRegistry);

        DefaultCommandDispatcher<String> dispatcher = new DefaultCommandDispatcher<String>(
            new CommandRegistry(),
            new ArgumentParser(resolverRegistry, providedValueRegistry),
            new SuggestionEngine<String>(new DefaultSuggestionRegistry<String>())
        );

        dispatcher.register(Collections.singletonList(CommandDefinition.builder()
            .name("cargos")
            .usage("cargos <jogador>")
            .parameters(Collections.singletonList(ParameterDefinition.builder()
                .parameterType(String.class)
                .name("jogador")
                .sourceKind(ParameterSourceKind.ARGUMENT)
                .resolverBinding(String.class)
                .build()))
            .handler(new CommandHandler() {
                @Override
                public Object invoke(me.lukavns.commandframework.api.context.CommandContext<?> context, Object[] arguments) {
                    return null;
                }

                @Override
                public String declaringType() {
                    return DefaultCommandDispatcherTest.class.getName();
                }

                @Override
                public String methodName() {
                    return "testHandler";
                }
            })
            .build()));

        TestContext context = new TestContext("tester");

        try {
            dispatcher.dispatch(context);
        } catch (ArgumentParseException exception) {
            assertTrue(context.command().isPresent());
            assertEquals("cargos <jogador>", context.command().get().usage());
            return;
        }

        throw new AssertionError("Expected an ArgumentParseException");
    }

    private static final class TestContext extends BaseCommandContext<String> {

        private TestContext(String sender) {
            super(sender, "cargos", Collections.<String>emptyList(), Collections.<Class<?>, Object>emptyMap());
        }

        @Override
        public boolean hasPermission(String permission) {
            return true;
        }

        @Override
        public void sendMessage(String message) {
        }

        @Override
        public void sendMessage(String[] messages) {
        }
    }
}
