package com.hartwig.serve.datamodel.gene;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class KnownGeneComparatorTest {

    @Test
    public void canSortKnownGenes() {
        KnownGene gene1 = GeneTestFactory.knownGeneBuilder().gene("A").build();
        KnownGene gene2 = GeneTestFactory.knownGeneBuilder().gene("B").build();

        List<KnownGene> genes = Lists.newArrayList(gene2, gene1);
        genes.sort(new KnownGeneComparator());

        assertEquals(gene1, genes.get(0));
        assertEquals(gene2, genes.get(1));
    }
}