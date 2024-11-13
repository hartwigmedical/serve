package com.hartwig.serve.datamodel.molecular;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableEventComparatorTest {

    @Test
    public void canSortActionableEvents() {
        ActionableEvent actionableEvent1 = create(LocalDate.of(2024, 1, 1), "event 1", createUrls("url 1", "url 2"));
        ActionableEvent actionableEvent2 = create(LocalDate.of(2023, 1, 1), "event 1", createUrls("url 1", "url 2"));
        ActionableEvent actionableEvent3 = create(LocalDate.of(2023, 1, 1), "event 1", createUrls("url 1"));
        ActionableEvent actionableEvent4 = create(LocalDate.of(2024, 1, 1), "event 2", createUrls("url 1", "url 2"));

        List<ActionableEvent> actionableEvents = Lists.newArrayList(actionableEvent3, actionableEvent1, actionableEvent4, actionableEvent2);
        actionableEvents.sort(new ActionableEventComparator());

        assertEquals(actionableEvent1, actionableEvents.get(0));
        assertEquals(actionableEvent2, actionableEvents.get(1));
        assertEquals(actionableEvent3, actionableEvents.get(2));
        assertEquals(actionableEvent4, actionableEvents.get(3));
    }

    @NotNull
    private static Set<String> createUrls(@NotNull String... strings) {
        return new TreeSet<>(Set.of(strings));
    }

    @NotNull
    private static ActionableEvent create(@NotNull LocalDate sourceDate, @NotNull String sourceEvent, @NotNull Set<String> sourceUrls) {
        return new ActionableEvent() {
            @NotNull
            @Override
            public LocalDate sourceDate() {
                return sourceDate;
            }

            @NotNull
            @Override
            public String sourceEvent() {
                return sourceEvent;
            }

            @NotNull
            @Override
            public Set<String> sourceUrls() {
                return sourceUrls;
            }

            @Override
            public String toString() {
                return "Event '" + sourceEvent + "' on " + sourceDate + " with URLs " + sourceUrls;
            }
        };
    }
}