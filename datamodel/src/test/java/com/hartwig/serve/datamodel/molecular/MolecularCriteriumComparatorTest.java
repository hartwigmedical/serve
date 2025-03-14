package com.hartwig.serve.datamodel.molecular;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MolecularCriteriumComparatorTest {

    @Test
    public void canSortMolecularCriteria() {
        MolecularCriterium criterium1 = MolecularCriteriumTestFactory.createWithTestActionableHotspot();
        MolecularCriterium criterium2 = MolecularCriteriumTestFactory.createWithTestActionableGene();
        MolecularCriterium criterium3 = MolecularCriteriumTestFactory.createWithTestActionableCharacteristic();

        List<MolecularCriterium> criteria = new ArrayList<>(List.of(criterium2, criterium3, criterium1));
        criteria.sort(new MolecularCriteriumComparator());

        assertEquals(criterium1, criteria.get(0));
        assertEquals(criterium2, criteria.get(1));
        assertEquals(criterium3, criteria.get(2));
    }
}