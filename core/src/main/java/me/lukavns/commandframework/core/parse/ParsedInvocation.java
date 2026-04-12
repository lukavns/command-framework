package me.lukavns.commandframework.core.parse;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ParsedInvocation {

    private final Object[] arguments;
    private final Map<String, Object> values;

    ParsedInvocation(Object[] arguments, Map<String, Object> values) {
        this.arguments = arguments;
        this.values = Collections.unmodifiableMap(new LinkedHashMap<String, Object>(values));
    }

    public Object[] arguments() {
        return this.arguments;
    }

    public Map<String, Object> values() {
        return this.values;
    }
}
