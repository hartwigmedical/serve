package com.hartwig.serve.extraction.exon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.range.RangeTestFactory;

import org.junit.Test;

public class ExonConsolidationTest {

    @Test
    public void canConsolidateEmptyExons() {
        assertTrue(ExonConsolidation.consolidate(Lists.newArrayList()).isEmpty());
    }

    @Test
    public void canConsolidateKnownExons() {
        KnownExon exon1 =
                RangeTestFactory.knownExonBuilder().addSources(Knowledgebase.VICC_CGI).proteinEffect(ProteinEffect.UNKNOWN).build();

        KnownExon exon2 = RangeTestFactory.knownExonBuilder()
                .addSources(Knowledgebase.VICC_CIVIC)
                .proteinEffect(ProteinEffect.LOSS_OF_FUNCTION)
                .build();

        Set<KnownExon> consolidated = ExonConsolidation.consolidate(Lists.newArrayList(exon1, exon2));
        assertEquals(1, consolidated.size());

        KnownExon exon = consolidated.iterator().next();
        assertEquals(2, exon.sources().size());
        assertTrue(exon.sources().contains(Knowledgebase.VICC_CGI));
        assertTrue(exon.sources().contains(Knowledgebase.VICC_CIVIC));
        assertEquals(ProteinEffect.LOSS_OF_FUNCTION, exon.proteinEffect());
    }
}