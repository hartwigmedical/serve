package com.hartwig.serve.datamodel;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class ActionableEventComparator implements Comparator<ActionableEvent> {

    @Override
    public int compare(@NotNull ActionableEvent event1, @NotNull ActionableEvent event2) {
        int dateCompare = event1.sourceDate().toString().compareTo(event2.sourceDate().toString());
        if (dateCompare != 0) {
            return dateCompare;
        }

        int eventCompare = event1.sourceEvent().compareTo(event2.sourceEvent());
        if (eventCompare != 0) {
            return eventCompare;
        }

        return event1.sourceUrls().toString().compareTo(event2.sourceUrls().toString());
    }
}
