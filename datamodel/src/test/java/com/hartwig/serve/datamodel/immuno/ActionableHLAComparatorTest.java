package com.hartwig.serve.datamodel.immuno;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class ActionableHLAComparatorTest {

    @Test
    public void canSortActionableHLA() {
        ActionableHLA hla1 = ImmunoTestFactory.actionableHLABuilder().hlaAllele("A1").sourceEvent("event 1").build();
        ActionableHLA hla2 = ImmunoTestFactory.actionableHLABuilder().hlaAllele("A1").sourceEvent("event 2").build();
        ActionableHLA hla3 = ImmunoTestFactory.actionableHLABuilder().hlaAllele("A2").sourceEvent("event 1").build();

        List<ActionableHLA> hlas = Lists.newArrayList(hla1, hla3, hla2);
        hlas.sort(new ActionableHLAComparator());

        assertEquals(hla1, hlas.get(0));
        assertEquals(hla2, hlas.get(1));
        assertEquals(hla3, hlas.get(2));
    }
}