package com.hartwig.serve.datamodel.gene;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class KnownGeneComparator implements Comparator<KnownGene> {

    @Override
    public int compare(@NotNull KnownGene gene1, @NotNull KnownGene gene2) {
        return gene1.gene().compareTo(gene2.gene());
    }
}
