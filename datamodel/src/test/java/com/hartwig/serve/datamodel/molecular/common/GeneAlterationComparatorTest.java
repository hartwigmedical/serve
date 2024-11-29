package com.hartwig.serve.datamodel.molecular.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GeneAlterationComparatorTest {

    @Test
    public void canSortGeneAlterations() {
        GeneAlteration alteration1 = CommonTestFactory.createGeneAlteration(GeneRole.ONCO, ProteinEffect.GAIN_OF_FUNCTION, null);
        GeneAlteration alteration2 = CommonTestFactory.createGeneAlteration(GeneRole.ONCO, ProteinEffect.NO_EFFECT, true);
        GeneAlteration alteration3 = CommonTestFactory.createGeneAlteration(GeneRole.ONCO, ProteinEffect.NO_EFFECT, null);
        GeneAlteration alteration4 = CommonTestFactory.createGeneAlteration(GeneRole.TSG, ProteinEffect.GAIN_OF_FUNCTION, null);
        GeneAlteration alteration5 = CommonTestFactory.createGeneAlteration(GeneRole.TSG, ProteinEffect.NO_EFFECT, true);
        GeneAlteration alteration6 = CommonTestFactory.createGeneAlteration(GeneRole.TSG, ProteinEffect.NO_EFFECT, null);

        List<GeneAlteration> alterations = new ArrayList<>(
            List.of(alteration5, alteration4, alteration3, alteration6, alteration1, alteration2)
        );
        alterations.sort(new GeneAlterationComparator());

        assertEquals(alteration1, alterations.get(0));
        assertEquals(alteration2, alterations.get(1));
        assertEquals(alteration3, alterations.get(2));
        assertEquals(alteration4, alterations.get(3));
        assertEquals(alteration5, alterations.get(4));
        assertEquals(alteration6, alterations.get(5));
    }
}