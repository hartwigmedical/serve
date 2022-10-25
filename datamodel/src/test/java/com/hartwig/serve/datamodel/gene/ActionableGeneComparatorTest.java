package com.hartwig.serve.datamodel.gene;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableGeneComparatorTest {

    @Test
    public void canSortActionableGenes() {
        ActionableGene gene1 = create("gene 1", GeneLevelEvent.ANY_MUTATION, "event 1");
        ActionableGene gene2 = create("gene 1", GeneLevelEvent.DELETION, "event 1");
        ActionableGene gene3 = create("gene 1", GeneLevelEvent.ANY_MUTATION, "event 2");
        ActionableGene gene4 = create("gene 2", GeneLevelEvent.ANY_MUTATION, "event 1");

        List<ActionableGene> genes = Lists.newArrayList(gene1, gene2, gene3, gene4);
        genes.sort(new ActionableGeneComparator());

        assertEquals(gene1, genes.get(0));
        assertEquals(gene3, genes.get(1));
        assertEquals(gene2, genes.get(2));
        assertEquals(gene4, genes.get(3));
    }

    @NotNull
    private static ActionableGene create(@NotNull String gene, @NotNull GeneLevelEvent event, @NotNull String sourceEvent) {
        return GeneTestFactory.actionableGeneBuilder().gene(gene).event(event).sourceEvent(sourceEvent).build();
    }
}