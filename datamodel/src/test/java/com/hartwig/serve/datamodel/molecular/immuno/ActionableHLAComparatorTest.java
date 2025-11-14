package com.hartwig.serve.datamodel.molecular.immuno;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ActionableHLAComparatorTest {

    @Test
    public void canSortActionableHLA() {
        ActionableHLA hla1 = ImmunoTestFactory.actionableHLABuilder().gene("A").alleleGroup("01").sourceEvent("event1").build();
        ActionableHLA hla2 = ImmunoTestFactory.actionableHLABuilder().gene("A").alleleGroup("02").sourceEvent("event1").build();
        ActionableHLA hla3 = ImmunoTestFactory.actionableHLABuilder().gene("A").alleleGroup("02").sourceEvent("event2").build();
        ActionableHLA hla4 = ImmunoTestFactory.actionableHLABuilder().gene("B").alleleGroup("01").sourceEvent("event1").build();
        ActionableHLA hla5 = ImmunoTestFactory.actionableHLABuilder().gene("B").alleleGroup("02").sourceEvent("event1").build();

        List<ActionableHLA> hlas = new ArrayList<>(List.of(hla1, hla5, hla3, hla2, hla4));
        hlas.sort(new ActionableHLAComparator());

        assertEquals(hla1, hlas.get(0));
        assertEquals(hla2, hlas.get(1));
        assertEquals(hla3, hlas.get(2));
        assertEquals(hla4, hlas.get(3));
        assertEquals(hla5, hlas.get(4));
    }
}