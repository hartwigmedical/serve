package com.hartwig.serve.datamodel.range;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KnownCodonComparatorTest {

    @Test
    public void canSortKnownCodons() {
        KnownCodon codon1 = create(RangeTestFactory.codonAnnotationBuilder().chromosome("1").build());
        KnownCodon codon2 = create(RangeTestFactory.codonAnnotationBuilder().chromosome("2").build());

        List<KnownCodon> codons = Lists.newArrayList(codon2, codon1);
        codons.sort(new KnownCodonComparator());

        assertEquals(codon1, codons.get(0));
        assertEquals(codon2, codons.get(1));
    }

    @NotNull
    private static KnownCodon create(@NotNull CodonAnnotation annotation) {
        return ImmutableKnownCodon.builder().annotation(annotation).build();
    }
}