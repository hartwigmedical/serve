package com.hartwig.serve.extraction.gene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.gene.ImmutableKnownGene;
import com.hartwig.serve.datamodel.gene.KnownGene;

import org.junit.Test;

public class GeneConsolidationTest {

    private static final String GENE = "gene";

    @Test
    public void canConsolidateEmptyGenes() {
        assertTrue(GeneConsolidation.consolidate(Lists.newArrayList()).isEmpty());
    }

    @Test
    public void canConsolidateKnownGenes() {
        KnownGene gene1 = ImmutableKnownGene.builder().gene(GENE).geneRole(GeneRole.ONCO).addSources(Knowledgebase.VICC_CIVIC).build();
        KnownGene gene2 = ImmutableKnownGene.builder().gene(GENE).geneRole(GeneRole.ONCO).addSources(Knowledgebase.VICC_CGI).build();

        Set<KnownGene> consolidated = GeneConsolidation.consolidate(Lists.newArrayList(gene1, gene2));
        assertEquals(1, consolidated.size());

        KnownGene gene = consolidated.iterator().next();
        assertEquals(2, gene.sources().size());
        assertEquals(gene.gene(), GENE);
        assertEquals(gene.geneRole(), GeneRole.ONCO);
        assertTrue(gene.sources().contains(Knowledgebase.VICC_CGI));
        assertTrue(gene.sources().contains(Knowledgebase.VICC_CIVIC));
    }
}