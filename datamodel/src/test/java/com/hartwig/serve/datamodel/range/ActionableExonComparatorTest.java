package com.hartwig.serve.datamodel.range;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.List;

public class ActionableExonComparatorTest {

    @Test
    public void canSortActionableExons() {
        ActionableExon exon1 = RangeTestFactory.actionableExonBuilder().chromosome("1").level(EvidenceLevel.A).build();
        ActionableExon exon2 = RangeTestFactory.actionableExonBuilder().chromosome("1").level(EvidenceLevel.B).build();
        ActionableExon exon3 = RangeTestFactory.actionableExonBuilder().chromosome("X").level(EvidenceLevel.A).build();

        List<ActionableExon> ranges = Lists.newArrayList(exon3, exon2, exon1);
        ranges.sort(new ActionableExonComparator());

        assertEquals(exon1, ranges.get(0));
        assertEquals(exon2, ranges.get(1));
        assertEquals(exon3, ranges.get(2));
    }

}