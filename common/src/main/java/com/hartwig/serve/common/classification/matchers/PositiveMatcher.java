package com.hartwig.serve.common.classification.matchers;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PositiveMatcher implements EventMatcher {
    @NotNull
    private final Set<String> positiveEvents;
    @NotNull
    private final Set<String> positiveBlacklistEvents;

    public PositiveMatcher(@NotNull final Set<String> positiveEvents, @NotNull final Set<String> positiveBlacklistEvents) {
        this.positiveEvents = positiveEvents;
        this.positiveBlacklistEvents = positiveBlacklistEvents;
    }

    @Override
    public boolean matches(@NotNull String gene, @NotNull String event) {
        for (String keyPhrase : positiveBlacklistEvents) {
            if (event.contains(keyPhrase)) {
                return false;
            }
        }

        for (String keyPhrase : positiveEvents) {
            if (event.contains(keyPhrase)) {
                return true;
            }
        }

        // If the event matches the gene we assume it's a gene level event
        return event.trim().equals(gene);
    }
}