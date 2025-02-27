package com.hartwig.serve.datamodel.molecular.gene;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableGeneComparatorTest {

    @Test
    public void canSortActionableGenes() {
        ActionableGene gene1 = create("gene 1", GeneEvent.ANY_MUTATION, "event1");
        ActionableGene gene2 = create("gene 1", GeneEvent.ANY_MUTATION, "event2");
        ActionableGene gene3 = create("gene 1", GeneEvent.DELETION, "event3");
        ActionableGene gene4 = create("gene 2", GeneEvent.ANY_MUTATION, "event4");

        List<ActionableGene> genes = new ArrayList<>(List.of(gene1, gene4, gene3, gene2));
        genes.sort(new ActionableGeneComparator());

        assertEquals(gene1, genes.get(0));
        assertEquals(gene2, genes.get(1));
        assertEquals(gene3, genes.get(2));
        assertEquals(gene4, genes.get(3));
    }

    @NotNull
    private static ActionableGene create(@NotNull String gene, @NotNull GeneEvent event, @NotNull String sourceEvent) {
        return GeneTestFactory.actionableGeneBuilder().gene(gene).event(event).sourceEvent(sourceEvent).build();
    }
}