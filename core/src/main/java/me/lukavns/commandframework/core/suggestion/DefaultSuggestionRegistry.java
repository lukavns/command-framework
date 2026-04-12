package me.lukavns.commandframework.core.suggestion;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import me.lukavns.commandframework.api.suggestion.SuggestionProvider;

public final class DefaultSuggestionRegistry<S> {

    private final Map<String, SuggestionProvider<S>> providers = new LinkedHashMap<String, SuggestionProvider<S>>();

    public void register(String name, SuggestionProvider<S> provider) {
        this.providers.put(normalize(name), provider);
    }

    public Optional<SuggestionProvider<S>> find(String name) {
        return Optional.ofNullable(this.providers.get(normalize(name)));
    }

    private String normalize(String input) {
        return input.toLowerCase(Locale.ROOT);
    }
}
