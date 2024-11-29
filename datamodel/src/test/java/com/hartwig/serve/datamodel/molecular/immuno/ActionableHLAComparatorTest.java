package com.hartwig.serve.datamodel.molecular.immuno;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ActionableHLAComparatorTest {

    @Test
    public void canSortActionableHLA() {
        ActionableHLA hla1 = ImmunoTestFactory.actionableHLABuilder().hlaAllele("A1").sourceEvent("event1").build();
        ActionableHLA hla2 = ImmunoTestFactory.actionableHLABuilder().hlaAllele("A1").sourceEvent("event1").build();
        ActionableHLA hla3 = ImmunoTestFactory.actionableHLABuilder().hlaAllele("A2").sourceEvent("event1").build();

        List<ActionableHLA> hlas = new ArrayList<>(List.of(hla1, hla3, hla2));
        hlas.sort(new ActionableHLAComparator());

        assertEquals(hla1, hlas.get(0));
        assertEquals(hla2, hlas.get(1));
        assertEquals(hla3, hlas.get(2));
    }
}