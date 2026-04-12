package me.lukavns.commandframework.core.util;

import java.util.List;
import me.lukavns.commandframework.api.command.ParameterDefinition;
import me.lukavns.commandframework.api.command.ParameterSourceKind;

public final class UsageBuilder {

    private UsageBuilder() {
    }

    public static String build(String rootName, List<String> subcommandPath, List<ParameterDefinition> parameters) {
        StringBuilder builder = new StringBuilder("/");
        builder.append(rootName);
        for (String segment : subcommandPath) {
            builder.append(' ').append(segment);
        }
        for (ParameterDefinition parameter : parameters) {
            if (parameter.sourceKind() != ParameterSourceKind.ARGUMENT) {
                continue;
            }
            builder.append(' ');
            builder.append(parameter.optional() ? '[' : '<');
            builder.append(parameter.name());
            if (parameter.remaining()) {
                builder.append("...");
            }
            builder.append(parameter.optional() ? ']' : '>');
        }
        return builder.toString();
    }
}
