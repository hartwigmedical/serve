package com.hartwig.serve.datamodel.common;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class GeneAlterationComparatorTest {

    @Test
    public void canSortGeneAlterations() {
        GeneAlteration alteration1 = CommonTestFactory.createGeneAlteration("A", GeneRole.ONCO, ProteinEffect.GAIN_OF_FUNCTION);
        GeneAlteration alteration2 = CommonTestFactory.createGeneAlteration("A", GeneRole.ONCO, ProteinEffect.NO_EFFECT);
        GeneAlteration alteration3 = CommonTestFactory.createGeneAlteration("A", GeneRole.TSG, ProteinEffect.GAIN_OF_FUNCTION);
        GeneAlteration alteration4 = CommonTestFactory.createGeneAlteration("A", GeneRole.TSG, ProteinEffect.NO_EFFECT);
        GeneAlteration alteration5 = CommonTestFactory.createGeneAlteration("B", GeneRole.TSG, ProteinEffect.GAIN_OF_FUNCTION);
        GeneAlteration alteration6 = CommonTestFactory.createGeneAlteration("B", GeneRole.TSG, ProteinEffect.NO_EFFECT);

        List<GeneAlteration> alterations = Lists.newArrayList(alteration5, alteration4, alteration3, alteration6, alteration1, alteration2);
        alterations.sort(new GeneAlterationComparator());

        assertEquals(alteration1, alterations.get(0));
        assertEquals(alteration2, alterations.get(1));
        assertEquals(alteration3, alterations.get(2));
        assertEquals(alteration4, alterations.get(3));
        assertEquals(alteration5, alterations.get(4));
        assertEquals(alteration6, alterations.get(5));
    }
}