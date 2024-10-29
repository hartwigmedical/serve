package com.hartwig.serve.datamodel.fusion;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class ActionableFusionComparatorTest {

    @Test
    public void canSortActionableFusions() {
        ActionableFusion fusion1 = FusionTestFactory.actionableFusionBuilder().geneUp("X").sourceEvent("event1").build();
        ActionableFusion fusion2 = FusionTestFactory.actionableFusionBuilder().geneUp("X").sourceEvent("event2").build();
        ActionableFusion fusion3 = FusionTestFactory.actionableFusionBuilder().geneUp("Z").sourceEvent("event3").build();

        List<ActionableFusion> fusions = Lists.newArrayList(fusion2, fusion1, fusion3);
        fusions.sort(new ActionableFusionComparator());

        assertEquals(fusion1, fusions.get(0));
        assertEquals(fusion2, fusions.get(1));
        assertEquals(fusion3, fusions.get(2));
    }
}