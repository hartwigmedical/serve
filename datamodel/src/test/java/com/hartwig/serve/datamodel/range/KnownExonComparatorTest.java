package com.hartwig.serve.datamodel.range;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class KnownExonComparatorTest {

    @Test
    public void canSortKnownExons() {
        KnownExon exon1 = RangeTestFactory.knownExonBuilder().chromosome("1").build();
        KnownExon exon2 = RangeTestFactory.knownExonBuilder().chromosome("2").build();

        List<KnownExon> exons = Lists.newArrayList(exon2, exon1);
        exons.sort(new KnownExonComparator());

        assertEquals(exon1, exons.get(0));
        assertEquals(exon2, exons.get(1));
    }
}