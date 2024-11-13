package com.hartwig.serve.extraction.fusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.molecular.fusion.KnownFusion;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class FusionConsolidationTest {

    @Test
    public void canConsolidateKnownFusions() {
        String gene1 = "gene1";
        String gene2 = "gene2";
        KnownFusion fusion1 = FusionTestFactory.knownFusionBuilder()
                .geneUp(gene1)
                .geneDown(gene2)
                .addSources(Knowledgebase.VICC_ONCOKB, Knowledgebase.VICC_CIVIC)
                .proteinEffect(ProteinEffect.AMBIGUOUS)
                .build();
        KnownFusion fusion2 =
                FusionTestFactory.knownFusionBuilder().geneUp(gene1).geneDown(gene2).addSources(Knowledgebase.VICC_CGI).build();
        KnownFusion fusion3 =
                FusionTestFactory.knownFusionBuilder().geneUp(gene2).geneDown(gene1).addSources(Knowledgebase.VICC_CGI).build();

        List<KnownFusion> consolidated = Lists.newArrayList(FusionConsolidation.consolidate(Lists.newArrayList(fusion1, fusion2, fusion3)));
        assertEquals(2, consolidated.size());

        KnownFusion gene1Fusion = findByGeneUp(consolidated, gene1);
        assertEquals(3, gene1Fusion.sources().size());
        assertTrue(gene1Fusion.sources().contains(Knowledgebase.VICC_CGI));
        assertTrue(gene1Fusion.sources().contains(Knowledgebase.VICC_ONCOKB));
        assertTrue(gene1Fusion.sources().contains(Knowledgebase.VICC_CIVIC));
        assertEquals(ProteinEffect.AMBIGUOUS, gene1Fusion.proteinEffect());

        KnownFusion gene2Fusion = findByGeneUp(consolidated, gene2);
        assertEquals(1, gene2Fusion.sources().size());
        assertTrue(gene2Fusion.sources().contains(Knowledgebase.VICC_CGI));
    }

    @NotNull
    private static KnownFusion findByGeneUp(@NotNull List<KnownFusion> fusions, @NotNull String gene) {
        for (KnownFusion fusion : fusions) {
            if (fusion.geneUp().equals(gene)) {
                return fusion;
            }
        }

        throw new IllegalStateException("Could not find gene in fusion pairs: " + gene);
    }
}