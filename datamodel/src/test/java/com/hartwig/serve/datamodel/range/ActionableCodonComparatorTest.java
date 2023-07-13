package com.hartwig.serve.datamodel.range;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;
import org.junit.Test;

import java.util.List;

public class ActionableCodonComparatorTest  {

    @Test
    public void canSortActionableCodons() {
        ActionableCodon codon1 = RangeTestFactory.actionableCodonBuilder().chromosome("1").level(EvidenceLevel.A).build();
        ActionableCodon codon2 = RangeTestFactory.actionableCodonBuilder().chromosome("1").level(EvidenceLevel.B).build();
        ActionableCodon codon3 = RangeTestFactory.actionableCodonBuilder().chromosome("X").level(EvidenceLevel.A).build();

        List<ActionableCodon> ranges = Lists.newArrayList(codon3, codon2, codon1);
        ranges.sort(new ActionableCodonComparator());

        assertEquals(codon1, ranges.get(0));
        assertEquals(codon2, ranges.get(1));
        assertEquals(codon3, ranges.get(2));
    }
}