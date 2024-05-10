package com.hartwig.serve.common.classification.matchers;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class NegativeMatcher implements EventMatcher {
    @NotNull
    private final Set<String> negativeEvents;
    @NotNull
    private final Set<String> negativeBlacklistEvents;

    public NegativeMatcher(@NotNull final Set<String> negativeEvents, @NotNull final Set<String> negativeBlacklistEvents) {
        this.negativeEvents = negativeEvents;
        this.negativeBlacklistEvents = negativeBlacklistEvents;
    }

    @Override
    public boolean matches(@NotNull String gene, @NotNull String event) {
        for (String keyPhrase : negativeBlacklistEvents) {
            if (event.contains(keyPhrase)) {
                return false;
            }
        }

        for (String keyPhrase : negativeEvents) {
            if (event.contains(keyPhrase)) {
                return true;
            }
        }

        // If the event matches the gene we assume it's a gene level event
        return event.trim().equals(gene);
    }
}