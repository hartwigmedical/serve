package com.hartwig.serve.common.classification.matchers;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

public class PresenceAbsenceOfProteinMatcher implements EventMatcher {
    @NotNull
    private final Set<String> events;
    @NotNull
    private final Set<String> blacklistEvents;

    public PresenceAbsenceOfProteinMatcher(@NotNull final Set<String> events, @NotNull final Set<String> blacklistEvents) {
        this.events = events;
        this.blacklistEvents = blacklistEvents;
    }

    @Override
    public boolean matches(@NotNull String gene, @NotNull String event) {
        for (String keyPhrase : blacklistEvents) {
            if (event.contains(keyPhrase)) {
                return false;
            }
        }

        for (String keyPhrase : events) {
            if (event.contains(keyPhrase)) {
                return true;
            }
        }

        // If the event matches the gene we assume it's a gene level event
        return event.trim().equals(gene);
    }
}