package com.hartwig.serve.datamodel.molecular.gene;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class KnownGeneComparator implements Comparator<KnownGene> {

    @Override
    public int compare(@NotNull KnownGene gene1, @NotNull KnownGene gene2) {
        int geneCompare = gene1.gene().compareTo(gene2.gene());
        if (geneCompare != 0) {
            return geneCompare;
        }

        return gene1.geneRole().compareTo(gene2.geneRole());
    }
}
