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
        ActionableEvent event1 =
                create(Knowledgebase.CKB_EVIDENCE, "CancerA", EvidenceLevel.A, EvidenceDirection.RESISTANT, false, true, "treatment1");
        ActionableEvent event2 =
                create(Knowledgebase.CKB_EVIDENCE, "CancerA", EvidenceLevel.A, EvidenceDirection.RESPONSIVE, false, true, "treatment2");
        ActionableEvent event3 =
                create(Knowledgebase.CKB_EVIDENCE, "CancerA", EvidenceLevel.A, EvidenceDirection.RESPONSIVE, false, true, "treatment3");
        ActionableEvent event4 =
                create(Knowledgebase.CKB_EVIDENCE, "CancerB", EvidenceLevel.A, EvidenceDirection.RESPONSIVE, false, true, "treatment4");
        ActionableEvent event5 =
                create(Knowledgebase.CKB_EVIDENCE, "CancerA", EvidenceLevel.B, EvidenceDirection.RESPONSIVE, false, true, "treatment5");
        ActionableEvent event6 =
                create(Knowledgebase.CKB_TRIAL, "CancerA", EvidenceLevel.B, EvidenceDirection.RESPONSIVE, true, false, "treatment5");

        List<ActionableEvent> events = Lists.newArrayList(event3, event5, event1, event6, event4, event2);
        events.sort(new ActionableEventComparator());

        assertEquals(event1, events.get(0));
        assertEquals(event2, events.get(1));
        assertEquals(event3, events.get(2));
        assertEquals(event4, events.get(3));
        assertEquals(event6, events.get(4));
        assertEquals(event5, events.get(5));
    }

    @NotNull
    private static ActionableEvent create(@NotNull Knowledgebase source, @NotNull String applicableCancerType, @NotNull EvidenceLevel level,
            @NotNull EvidenceDirection direction, boolean isTrial, boolean isTreatment, @NotNull String treatmentName) {
        return DatamodelTestFactory.createActionableEvent(source,
                Strings.EMPTY,
                Sets.newHashSet(),
                DatamodelTestFactory.interventionBuilder(isTrial, isTreatment, treatmentName),
                DatamodelTestFactory.cancerTypeBuilder().name(applicableCancerType).build(),
                Sets.newHashSet(),
                level,
                direction,
                Sets.newHashSet());
    }
}