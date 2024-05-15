package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableEventComparatorTest {

    @Test
    public void canSortActionableEvents() {
        ActionableEvent event1 = create(Knowledgebase.VICC_CGI, "CancerA", EvidenceLevel.A, EvidenceDirection.RESISTANT);
        ActionableEvent event2 = create(Knowledgebase.VICC_CGI, "CancerA", EvidenceLevel.A, EvidenceDirection.RESPONSIVE);
        ActionableEvent event3 = create(Knowledgebase.VICC_CGI, "CancerA", EvidenceLevel.A, EvidenceDirection.RESPONSIVE);
        ActionableEvent event4 = create(Knowledgebase.VICC_CGI, "CancerB", EvidenceLevel.A, EvidenceDirection.RESPONSIVE);
        ActionableEvent event5 = create(Knowledgebase.VICC_CGI, "CancerA", EvidenceLevel.B, EvidenceDirection.RESISTANT);
        ActionableEvent event6 = create(Knowledgebase.VICC_CIVIC, "CancerA", EvidenceLevel.A, EvidenceDirection.RESISTANT);

        List<ActionableEvent> events = Lists.newArrayList(event3, event5, event1, event6, event4, event2);
        events.sort(new ActionableEventComparator());

        assertEquals(event1, events.get(0));
        assertEquals(event2, events.get(1));
        assertEquals(event3, events.get(2));
        assertEquals(event4, events.get(3));
        assertEquals(event5, events.get(4));
        assertEquals(event6, events.get(5));
    }

    @NotNull
    private static ActionableEvent create(@NotNull Knowledgebase source, @NotNull String applicableCancerType, @NotNull EvidenceLevel level,
            @NotNull EvidenceDirection direction) {
        return DatamodelTestFactory.createActionableEvent(source,
                Strings.EMPTY,
                Sets.newHashSet(),
                DatamodelTestFactory.interventionBuilder(true, false),
                DatamodelTestFactory.cancerTypeBuilder().name(applicableCancerType).build(),
                Sets.newHashSet(),
                level,
                direction,
                Sets.newHashSet());
    }
}