package com.hartwig.serve.datamodel.molecular.gene;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.molecular.common.GeneRole;

import org.junit.Test;

public class KnownGeneComparatorTest {

    @Test
    public void canSortKnownGenes() {
        KnownGene gene1 = GeneTestFactory.knownGeneBuilder().gene("A").geneRole(GeneRole.ONCO).build();
        KnownGene gene2 = GeneTestFactory.knownGeneBuilder().gene("A").geneRole(GeneRole.TSG).build();
        KnownGene gene3 = GeneTestFactory.knownGeneBuilder().gene("B").geneRole(GeneRole.ONCO).build();

        List<KnownGene> genes = Lists.newArrayList(gene2, gene3, gene1);
        genes.sort(new KnownGeneComparator());

        assertEquals(gene1, genes.get(0));
        assertEquals(gene2, genes.get(1));
        assertEquals(gene3, genes.get(2));
    }
}