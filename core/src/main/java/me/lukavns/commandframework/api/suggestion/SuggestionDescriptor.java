package me.lukavns.commandframework.api.suggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SuggestionDescriptor {

    private static final SuggestionDescriptor NONE = new SuggestionDescriptor("", Collections.<String>emptyList());

    private final String providerName;
    private final List<String> staticSuggestions;

    public SuggestionDescriptor(String providerName, List<String> staticSuggestions) {
        this.providerName = providerName == null ? "" : providerName;
        this.staticSuggestions = Collections.unmodifiableList(new ArrayList<String>(staticSuggestions));
    }

    public static SuggestionDescriptor none() {
        return NONE;
    }

    public String providerName() {
        return this.providerName;
    }

    public List<String> staticSuggestions() {
        return this.staticSuggestions;
    }

    public boolean hasProvider() {
        return !this.providerName.isEmpty();
    }

    public boolean hasStaticSuggestions() {
        return !this.staticSuggestions.isEmpty();
    }
}
