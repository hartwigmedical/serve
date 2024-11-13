package com.hartwig.serve.datamodel.molecular.range;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class KnownExonComparatorTest {

    @Test
    public void canSortKnownExons() {
        KnownExon exon1 = RangeTestFactory.knownExonBuilder().chromosome("1").inputTranscript("trans 1").inputExonRank(1).build();
        KnownExon exon2 = RangeTestFactory.knownExonBuilder().chromosome("1").inputTranscript("trans 1").inputExonRank(2).build();
        KnownExon exon3 = RangeTestFactory.knownExonBuilder().chromosome("1").inputTranscript("trans 2").inputExonRank(1).build();
        KnownExon exon4 = RangeTestFactory.knownExonBuilder().chromosome("2").inputTranscript("trans 1").inputExonRank(1).build();

        List<KnownExon> exons = Lists.newArrayList(exon2, exon1, exon4, exon3);
        exons.sort(new KnownExonComparator());

        assertEquals(exon1, exons.get(0));
        assertEquals(exon2, exons.get(1));
        assertEquals(exon3, exons.get(2));
        assertEquals(exon4, exons.get(3));
    }
}