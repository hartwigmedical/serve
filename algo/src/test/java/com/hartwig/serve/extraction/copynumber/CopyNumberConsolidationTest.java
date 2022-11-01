package com.hartwig.serve.extraction.copynumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.gene.GeneEvent;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CopyNumberConsolidationTest {

    @Test
    public void canConsolidateCopyNumbers() {
        String gene1 = "gene1";
        String gene2 = "gene2";
        KnownCopyNumber copyNumber1 = ampBuilder().gene(gene1).addSources(Knowledgebase.VICC_ONCOKB, Knowledgebase.VICC_CIVIC).build();
        KnownCopyNumber copyNumber2 =
                ampBuilder().gene(gene1).addSources(Knowledgebase.VICC_CGI).proteinEffect(ProteinEffect.AMBIGUOUS).build();
        KnownCopyNumber copyNumber3 = ampBuilder().gene(gene2).addSources(Knowledgebase.VICC_CGI).build();

        List<KnownCopyNumber> consolidated =
                Lists.newArrayList(CopyNumberConsolidation.consolidate(Lists.newArrayList(copyNumber1, copyNumber2, copyNumber3)));
        assertEquals(2, consolidated.size());

        KnownCopyNumber gene1CopyNumber = findByGene(consolidated, gene1);
        assertEquals(3, gene1CopyNumber.sources().size());
        assertTrue(gene1CopyNumber.sources().contains(Knowledgebase.VICC_CGI));
        assertTrue(gene1CopyNumber.sources().contains(Knowledgebase.VICC_ONCOKB));
        assertTrue(gene1CopyNumber.sources().contains(Knowledgebase.VICC_CIVIC));
        assertEquals(ProteinEffect.AMBIGUOUS, gene1CopyNumber.proteinEffect());

        KnownCopyNumber gene2CopyNumber = findByGene(consolidated, gene2);
        assertEquals(1, gene2CopyNumber.sources().size());
        assertTrue(gene2CopyNumber.sources().contains(Knowledgebase.VICC_CGI));
    }

    @NotNull
    private static ImmutableKnownCopyNumber.Builder ampBuilder() {
        return GeneTestFactory.knownCopyNumberBuilder().event(GeneEvent.AMPLIFICATION);
    }

    @NotNull
    private static KnownCopyNumber findByGene(@NotNull List<KnownCopyNumber> copyNumbers, @NotNull String gene) {
        for (KnownCopyNumber copyNumber : copyNumbers) {
            if (copyNumber.gene().equals(gene)) {
                return copyNumber;
            }
        }

        throw new IllegalStateException("Could not find gene in copy numbers: " + gene);
    }
}