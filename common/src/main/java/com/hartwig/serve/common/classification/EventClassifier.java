package com.hartwig.serve.common.classification;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.hartwig.serve.common.classification.matchers.EventMatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class EventClassifier {

    private static final Logger LOGGER = LogManager.getLogger(EventClassifier.class);

    @NotNull
    private final Map<EventType, EventMatcher> matchers;

    public EventClassifier(@NotNull final Map<EventType, EventMatcher> matchers) {
        this.matchers = matchers;
    }

    @NotNull
    public EventType determineType(@NotNull String gene, @NotNull String event) {
        Set<EventType> positiveTypes = matchers.entrySet().stream()
                .filter(entry -> entry.getValue().matches(gene, event))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (positiveTypes.size() > 1) {
            LOGGER.warn("More than one type evaluated to true for '{}' on '{}': {}", event, gene, positiveTypes);
        } else if (positiveTypes.size() == 1) {
            return positiveTypes.iterator().next();
        }

        return EventType.UNKNOWN;
    }
}
