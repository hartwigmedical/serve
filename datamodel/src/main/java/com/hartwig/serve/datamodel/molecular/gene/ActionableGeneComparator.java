package com.hartwig.serve.datamodel.molecular.gene;

import java.util.Comparator;

import com.hartwig.serve.datamodel.molecular.ActionableEvent;
import com.hartwig.serve.datamodel.molecular.ActionableEventComparator;

import org.jetbrains.annotations.NotNull;

public class ActionableGeneComparator implements Comparator<ActionableGene> {

    @NotNull
    private final Comparator<GeneAnnotation> geneAnnotationComparator = new GeneAnnotationComparator();
    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableGene gene1, @NotNull ActionableGene gene2) {
        int geneAnnotationCompare = geneAnnotationComparator.compare(gene1, gene2);
        if (geneAnnotationCompare != 0) {
            return geneAnnotationCompare;
        }

        return actionableEventComparator.compare(gene1, gene2);
    }
}
