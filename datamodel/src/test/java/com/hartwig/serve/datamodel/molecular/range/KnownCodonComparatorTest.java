package com.hartwig.serve.datamodel.molecular.range;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class KnownCodonComparatorTest {

    @Test
    public void canSortKnownCodons() {
        KnownCodon codon1 = RangeTestFactory.knownCodonBuilder().chromosome("1").inputTranscript("trans 1").inputCodonRank(1).build();
        KnownCodon codon2 = RangeTestFactory.knownCodonBuilder().chromosome("1").inputTranscript("trans 1").inputCodonRank(2).build();
        KnownCodon codon3 = RangeTestFactory.knownCodonBuilder().chromosome("1").inputTranscript("trans 2").inputCodonRank(1).build();
        KnownCodon codon4 = RangeTestFactory.knownCodonBuilder().chromosome("2").inputTranscript("trans 1").inputCodonRank(1).build();

        List<KnownCodon> codons = new ArrayList<>(List.of(codon3, codon4, codon2, codon1));
        codons.sort(new KnownCodonComparator());

        assertEquals(codon1, codons.get(0));
        assertEquals(codon2, codons.get(1));
        assertEquals(codon3, codons.get(2));
        assertEquals(codon4, codons.get(3));
    }
}