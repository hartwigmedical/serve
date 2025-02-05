package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriteriumTestFactory;

import org.junit.Test;

public class MolecularCriteriaCombinerTest {

    @Test
    public void shouldCombineCriteriaPair() {
        MolecularCriterium criterium1 = MolecularCriteriumTestFactory.createWithTestActionableHotspot();
        MolecularCriterium criterium2 = MolecularCriteriumTestFactory.createWithTestActionableGene();

        MolecularCriterium combined = MolecularCriteriaCombiner.combine(criterium1, criterium2);

        assertEquals(combined.hotspots(), criterium1.hotspots());
        assertEquals(combined.genes(), criterium2.genes());
    }

    @Test
    public void shouldCombineCriteriaList() {
        MolecularCriterium criterium1 = MolecularCriteriumTestFactory.createWithTestActionableHotspot();
        MolecularCriterium criterium2 = MolecularCriteriumTestFactory.createWithTestActionableGene();
        MolecularCriterium criterium3 = MolecularCriteriumTestFactory.createWithTestActionableCharacteristic();

        MolecularCriterium combined = MolecularCriteriaCombiner.combine(List.of(criterium1, criterium2, criterium3));

        assertEquals(combined.hotspots(), criterium1.hotspots());
        assertEquals(combined.genes(), criterium2.genes());
        assertEquals(combined.characteristics(), criterium3.characteristics());
    }

    @Test
    public void shouldCombineEmptyCriteriaList() {
        MolecularCriterium combined = MolecularCriteriaCombiner.combine(List.of());
        assertEquals(ImmutableMolecularCriterium.builder().build(), combined);
    }

}