package com.hartwig.serve.datamodel.gene;

import java.util.Comparator;

import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.ActionableEventComparator;

import org.jetbrains.annotations.NotNull;

class ActionableGeneComparator implements Comparator<ActionableGene> {

    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableGene gene1, @NotNull ActionableGene gene2) {
        int geneCompare = gene1.gene().compareTo(gene2.gene());
        if (geneCompare != 0) {
            return geneCompare;
        }

        int eventCompare = gene1.event().toString().compareTo(gene2.event().toString());
        if (eventCompare != 0) {
            return eventCompare;
        }

        return actionableEventComparator.compare(gene1, gene2);
    }
}
