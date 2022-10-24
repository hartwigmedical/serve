package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class GeneAlterationComparatorTest {

    @Test
    public void canSortActionableGeneMutations() {
        GeneAlteration alteration1 = create("A", GeneRole.ONCO, ProteinEffect.NO_EFFECT);
        GeneAlteration alteration2 = create("A", GeneRole.ONCO, ProteinEffect.GAIN_OF_FUNCTION);
        GeneAlteration alteration3 = create("A", GeneRole.TSG, ProteinEffect.GAIN_OF_FUNCTION);
        GeneAlteration alteration4 = create("A", GeneRole.TSG, ProteinEffect.NO_EFFECT);
        GeneAlteration alteration5 = create("B", GeneRole.TSG, ProteinEffect.NO_EFFECT);
        GeneAlteration alteration6 = create("B", GeneRole.TSG, ProteinEffect.GAIN_OF_FUNCTION);

        List<GeneAlteration> alterations = Lists.newArrayList(alteration1, alteration2, alteration3, alteration4, alteration5, alteration6);
        alterations.sort(new GeneAlterationComparator());

        assertEquals(alteration2, alterations.get(0));
        assertEquals(alteration1, alterations.get(1));
        assertEquals(alteration3, alterations.get(2));
        assertEquals(alteration4, alterations.get(3));
        assertEquals(alteration6, alterations.get(4));
        assertEquals(alteration5, alterations.get(5));
    }

    @NotNull
    private static GeneAlteration create(@NotNull String gene, @NotNull GeneRole geneRole, @NotNull ProteinEffect proteinEffect) {
        return new GeneAlteration() {
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