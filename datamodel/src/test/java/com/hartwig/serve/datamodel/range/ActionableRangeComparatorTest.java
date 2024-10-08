package com.hartwig.serve.datamodel.range;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;

import org.junit.Test;

public class ActionableRangeComparatorTest {

    @Test
    public void canSortActionableRanges() {
        ActionableRange codon1 = RangeTestFactory.actionableRangeBuilder().chromosome("1").evidenceLevel(EvidenceLevel.A).build();
        ActionableRange codon2 = RangeTestFactory.actionableRangeBuilder().chromosome("1").evidenceLevel(EvidenceLevel.B).build();
        ActionableRange codon3 = RangeTestFactory.actionableRangeBuilder().chromosome("X").evidenceLevel(EvidenceLevel.A).build();

        List<ActionableRange> ranges = Lists.newArrayList(codon3, codon2, codon1);
        ranges.sort(new ActionableRangeComparator());

        assertEquals(codon1, ranges.get(0));
        assertEquals(codon2, ranges.get(1));
        assertEquals(codon3, ranges.get(2));
    }
}