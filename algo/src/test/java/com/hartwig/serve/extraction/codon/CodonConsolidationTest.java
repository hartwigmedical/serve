package com.hartwig.serve.extraction.codon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.RangeTestFactory;

import org.junit.Test;

public class CodonConsolidationTest {

    @Test
    public void canConsolidateEmptyCodons() {
        assertTrue(CodonConsolidation.consolidate(Lists.newArrayList()).isEmpty());
    }

    @Test
    public void canConsolidateKnownCodons() {
        KnownCodon codon1 = RangeTestFactory.knownCodonBuilder()
                .addSources(Knowledgebase.VICC_CGI)
                .proteinEffect(ProteinEffect.GAIN_OF_FUNCTION)
                .build();

        KnownCodon codon2 = RangeTestFactory.knownCodonBuilder()
                .addSources(Knowledgebase.VICC_CIVIC)
                .proteinEffect(ProteinEffect.LOSS_OF_FUNCTION)
                .build();

        Set<KnownCodon> consolidated = CodonConsolidation.consolidate(Lists.newArrayList(codon1, codon2));
        assertEquals(1, consolidated.size());

        KnownCodon codon = consolidated.iterator().next();
        assertEquals(2, codon.sources().size());
        assertTrue(codon.sources().contains(Knowledgebase.VICC_CGI));
        assertTrue(codon.sources().contains(Knowledgebase.VICC_CIVIC));
        assertEquals(ProteinEffect.AMBIGUOUS, codon.proteinEffect());
    }
}