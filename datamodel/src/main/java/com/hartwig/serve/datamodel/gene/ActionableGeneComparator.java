package com.hartwig.serve.datamodel.gene;

import java.util.Comparator;

import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.ActionableEventComparator;
import com.hartwig.serve.datamodel.common.GeneAlteration;
import com.hartwig.serve.datamodel.common.GeneAlterationComparator;

import org.jetbrains.annotations.NotNull;

public class ActionableGeneComparator implements Comparator<ActionableGene> {

    @NotNull
    private final Comparator<GeneAlteration> geneAlterationComparator = new GeneAlterationComparator();
    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableGene gene1, @NotNull ActionableGene gene2) {
        int geneAlterationCompare = geneAlterationComparator.compare(gene1, gene2);
        if (geneAlterationCompare != 0) {
            return geneAlterationCompare;
        }

        int eventCompare = gene1.event().toString().compareTo(gene2.event().toString());
        if (eventCompare != 0) {
            return eventCompare;
        }

        return actionableEventComparator.compare(gene1, gene2);
    }
}
