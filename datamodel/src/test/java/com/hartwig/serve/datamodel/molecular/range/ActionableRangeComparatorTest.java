package com.hartwig.serve.datamodel.molecular.range;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ActionableRangeComparatorTest {

    @Test
    public void canSortActionableRanges() {
        ActionableRange codon1 = RangeTestFactory.actionableRangeBuilder().chromosome("1").sourceEvent("event1").build();
        ActionableRange codon2 = RangeTestFactory.actionableRangeBuilder().chromosome("1").sourceEvent("event2").build();
        ActionableRange codon3 = RangeTestFactory.actionableRangeBuilder().chromosome("X").sourceEvent("event3").build();

        List<ActionableRange> ranges = new ArrayList<>(List.of(codon3, codon2, codon1));
        ranges.sort(new ActionableRangeComparator());

        assertEquals(codon1, ranges.get(0));
        assertEquals(codon2, ranges.get(1));
        assertEquals(codon3, ranges.get(2));
    }
}