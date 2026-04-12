package me.lukavns.commandframework.api.suggestion;

import java.util.List;

public interface SuggestionProvider<S> {

    List<String> suggest(SuggestionContext<S> context);
}
