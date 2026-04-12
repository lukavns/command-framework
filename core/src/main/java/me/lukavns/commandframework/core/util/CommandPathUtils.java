package me.lukavns.commandframework.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CommandPathUtils {

    private CommandPathUtils() {
    }

    public static List<String> split(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String normalized = input.trim().replace('.', ' ');
        String[] segments = normalized.split("\\s+");
        List<String> values = new ArrayList<String>();
        for (String segment : segments) {
            if (!segment.isEmpty()) {
                values.add(segment);
            }
        }
        return values;
    }
}
