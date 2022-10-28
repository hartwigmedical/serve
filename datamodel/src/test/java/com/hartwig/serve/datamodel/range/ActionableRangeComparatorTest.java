package com.hartwig.serve.datamodel.range;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;

import org.junit.Test;

public class ActionableRangeComparatorTest {

    @Test
    public void canSortActionableRanges() {
        ActionableRange range1 = RangeTestFactory.actionableRangeBuilder().chromosome("1").level(EvidenceLevel.A).build();
        ActionableRange range2 = RangeTestFactory.actionableRangeBuilder().chromosome("1").level(EvidenceLevel.B).build();
        ActionableRange range3 = RangeTestFactory.actionableRangeBuilder().chromosome("X").level(EvidenceLevel.A).build();

        List<ActionableRange> ranges = Lists.newArrayList(range3, range2, range1);
        ranges.sort(new ActionableRangeComparator());

        assertEquals(range1, ranges.get(0));
        assertEquals(range2, ranges.get(1));
        assertEquals(range3, ranges.get(2));
    }
}