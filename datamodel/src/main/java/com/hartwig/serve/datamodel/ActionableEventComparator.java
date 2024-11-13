package com.hartwig.serve.datamodel;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class ActionableEventComparator implements Comparator<ActionableEvent> {

    @Override
    public int compare(@NotNull ActionableEvent event1, @NotNull ActionableEvent event2) {
        int eventCompare = event1.sourceEvent().compareTo(event2.sourceEvent());
        if (eventCompare != 0) {
            return eventCompare;
        }

        int dateCompare = event2.sourceDate().compareTo(event1.sourceDate());
        if (dateCompare != 0) {
            return dateCompare;
        }

        return CompareFunctions.compareSetOfComparable(event1.sourceUrls(), event2.sourceUrls());
    }
}
