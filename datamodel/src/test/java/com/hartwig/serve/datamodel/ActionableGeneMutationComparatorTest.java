package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableGeneMutationComparatorTest {

    @Test
    public void canSortActionableGeneMutations() {
        ActionableGeneMutation geneMutation1 = create("A", GeneRole.ONCO, ProteinEffect.NO_EFFECT);
        ActionableGeneMutation geneMutation2 = create("A", GeneRole.ONCO, ProteinEffect.GAIN_OF_FUNCTION);
        ActionableGeneMutation geneMutation3 = create("A", GeneRole.TSG, ProteinEffect.GAIN_OF_FUNCTION);
        ActionableGeneMutation geneMutation4 = create("A", GeneRole.TSG, ProteinEffect.NO_EFFECT);
        ActionableGeneMutation geneMutation5 = create("B", GeneRole.TSG, ProteinEffect.NO_EFFECT);
        ActionableGeneMutation geneMutation6 = create("B", GeneRole.TSG, ProteinEffect.GAIN_OF_FUNCTION);

        List<ActionableGeneMutation> geneMutations =
                Lists.newArrayList(geneMutation1, geneMutation2, geneMutation3, geneMutation4, geneMutation5, geneMutation6);
        geneMutations.sort(new ActionableGeneMutationComparator());

        assertEquals(geneMutation2, geneMutations.get(0));
        assertEquals(geneMutation1, geneMutations.get(1));
        assertEquals(geneMutation3, geneMutations.get(2));
        assertEquals(geneMutation4, geneMutations.get(3));
        assertEquals(geneMutation6, geneMutations.get(4));
        assertEquals(geneMutation5, geneMutations.get(5));
    }

    @NotNull
    private static ActionableGeneMutation create(@NotNull String gene, @NotNull GeneRole geneRole, @NotNull ProteinEffect proteinEffect) {
        return new ActionableGeneMutation() {
            @NotNull
            @Override
            public String gene() {
                return gene;
            }

            @NotNull
            @Override
            public GeneRole geneRole() {
                return geneRole;
            }

            @NotNull
            @Override
            public ProteinEffect proteinEffect() {
                return proteinEffect;
            }

            @Override
            public String toString() {
                return gene + " - " + geneRole + " - " + proteinEffect;
            }
        };
    }
}