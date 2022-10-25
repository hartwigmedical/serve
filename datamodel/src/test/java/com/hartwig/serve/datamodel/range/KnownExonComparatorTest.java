package com.hartwig.serve.datamodel.range;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.MutationType;

import org.junit.Test;

public class KnownExonComparatorTest {

    @Test
    public void canSortKnownExons() {
        KnownExon exon1 = ImmutableKnownExon.builder()
                .annotation(ImmutableExonAnnotation.builder()
                        .from(RangeTestFactory.createTestExonAnnotation())
                        .chromosome("1")
                        .start(10)
                        .end(11)
                        .gene("gene x")
                        .applicableMutationType(MutationType.ANY)
                        .rank(1)
                        .transcript("transcript x")
                        .build())
                .build();

        KnownExon exon2 =
                ImmutableKnownExon.builder().annotation(ImmutableExonAnnotation.builder().from(exon1.annotation()).rank(2).build()).build();

        Set<KnownExon> sortedExons = Sets.newTreeSet(new KnownExonComparator());
        sortedExons.add(exon2);
        sortedExons.add(exon1);

        assertEquals(exon1, sortedExons.iterator().next());
    }
}