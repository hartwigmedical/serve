package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriteriumTestFactory;

import org.junit.Test;

public class CkbTrialExtractorTest {

    @Test
    public void shouldCombineRequiredWithPartiallyRequiredCriteria() {
        MolecularCriterium requiredCriterium = MolecularCriteriumTestFactory.createWithTestActionableHotspot();

        MolecularCriterium partial1 = MolecularCriteriumTestFactory.createWithTestActionableGene();
        MolecularCriterium partial2 = MolecularCriteriumTestFactory.createWithTestActionableCharacteristic();

        List<MolecularCriterium> partiallyRequiredCriterium = List.of(partial1, partial2);

        Set<MolecularCriterium> anyMolecularCriteria =
                CkbTrialExtractor.combinePartialWithRequired(requiredCriterium, partiallyRequiredCriterium);

        Set<MolecularCriterium> expected = Set.of(
                // TODO! fix due to
                //                ImmutableMolecularCriterium.builder().addAllHotspots(requiredCriterium.hotspots())
                //                        .addAllGenes(partial1.genes()).build(),
                //                ImmutableMolecularCriterium.builder().addAllHotspots(requiredCriterium.hotspots())
                //                        .addAllCharacteristics(partial2.characteristics()).build()
        );
        assertEquals(expected, anyMolecularCriteria);
    }

    @Test
    public void shouldHandleEmptyPartialCriteria() {
        MolecularCriterium requiredCriterium = MolecularCriteriumTestFactory.createWithTestActionableHotspot();
        List<MolecularCriterium> partiallyRequiredCriterium = List.of();

        Set<MolecularCriterium> anyMolecularCriteria =
                CkbTrialExtractor.combinePartialWithRequired(requiredCriterium, partiallyRequiredCriterium);

        assertEquals(Set.of(requiredCriterium), anyMolecularCriteria);
    }

    @Test
    public void shouldHandleEmptyRequiredCriteria() {
        MolecularCriterium requiredCriterium = ImmutableMolecularCriterium.builder().build();
        MolecularCriterium partial1 = MolecularCriteriumTestFactory.createWithTestActionableGene();
        MolecularCriterium partial2 = MolecularCriteriumTestFactory.createWithTestActionableCharacteristic();

        List<MolecularCriterium> partiallyRequiredCriterium = List.of(partial1, partial2);

        Set<MolecularCriterium> anyMolecularCriteria =
                CkbTrialExtractor.combinePartialWithRequired(requiredCriterium, partiallyRequiredCriterium);

        assertEquals(Set.of(partial1, partial2), anyMolecularCriteria);
    }
}
