package com.hartwig.serve.datamodel;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class ActionableGeneMutationComparator implements Comparator<ActionableGeneMutation> {

    @Override
    public int compare(@NotNull ActionableGeneMutation geneMutation1, @NotNull ActionableGeneMutation geneMutation2) {
        int geneCompare = geneMutation1.gene().compareTo(geneMutation2.gene());
        if (geneCompare != 0) {
            return geneCompare;
        }

        int geneRoleCompare = geneMutation1.geneRole().toString().compareTo(geneMutation2.geneRole().toString());
        if (geneRoleCompare != 0) {
            return geneRoleCompare;
        }

        return geneMutation1.proteinEffect().toString().compareTo(geneMutation2.proteinEffect().toString());
    }
}
