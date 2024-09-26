package com.hartwig.serve.datamodel.gene;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableGeneComparatorTest {

    @Test
    public void canSortActionableGenes() {
        ActionableGene gene1 = create("gene 1", GeneEvent.ANY_MUTATION, EvidenceLevel.A);
        ActionableGene gene2 = create("gene 1", GeneEvent.ANY_MUTATION, EvidenceLevel.B);
        ActionableGene gene3 = create("gene 1", GeneEvent.DELETION, EvidenceLevel.A);
        ActionableGene gene4 = create("gene 2", GeneEvent.ANY_MUTATION, EvidenceLevel.A);

        List<ActionableGene> genes = Lists.newArrayList(gene1, gene4, gene3, gene2);
        genes.sort(new ActionableGeneComparator());

        assertEquals(gene1, genes.get(0));
        assertEquals(gene2, genes.get(1));
        assertEquals(gene3, genes.get(2));
        assertEquals(gene4, genes.get(3));
    }

    @NotNull
    private static ActionableGene create(@NotNull String gene, @NotNull GeneEvent event, @NotNull EvidenceLevel level) {
        return GeneTestFactory.actionableGeneBuilder().gene(gene).event(event).evidenceLevel(level).build();
    }
}