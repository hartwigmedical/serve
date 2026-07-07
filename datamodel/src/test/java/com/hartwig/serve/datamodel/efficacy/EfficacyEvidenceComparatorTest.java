package com.hartwig.serve.datamodel.efficacy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.molecular.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.molecular.gene.ActionableGene;
import com.hartwig.serve.datamodel.molecular.gene.GeneTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class EfficacyEvidenceComparatorTest {
    
    @Test
    public void canSortEfficacyEvidences() {
        EfficacyEvidence evidence1 = create("CancerA", EvidenceLevel.A, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESPONSIVE);
        EfficacyEvidence evidence2 = create("CancerA", EvidenceLevel.A, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESISTANT);
        EfficacyEvidence evidence3 = create("CancerA", EvidenceLevel.B, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESPONSIVE);
        EfficacyEvidence evidence4 = create("CancerB", EvidenceLevel.A, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESPONSIVE);

        List<EfficacyEvidence> efficacyEvidences = new ArrayList<>(List.of(evidence4, evidence1, evidence3, evidence2));
        efficacyEvidences.sort(new EfficacyEvidenceComparator());

        assertEquals(evidence1, efficacyEvidences.get(0));
        assertEquals(evidence2, efficacyEvidences.get(1));
        assertEquals(evidence3, efficacyEvidences.get(2));
        assertEquals(evidence4, efficacyEvidences.get(3));
    }

    @Test
    public void canSortEfficacyEvidenceWithCombinedMolecularProfile() {
        ActionableGene testGene1 = GeneTestFactory.createTestActionableGene();
        ActionableGene testGene2 = GeneTestFactory.createTestActionableGene();
        ActionableFusion testFusion = FusionTestFactory.createTestActionableFusion();

        EfficacyEvidence evidence1 = EfficacyEvidenceTestFactory.builder()
                .efficacyDescription("description 2")
                .molecularCriterium(ImmutableMolecularCriterium.builder()
                        .addGenes(testGene1)
                        .addGenes(testGene2)
                        .build())
                .build();
        
        EfficacyEvidence evidence2 = EfficacyEvidenceTestFactory.builder()
                .efficacyDescription("description 1")
                .molecularCriterium(ImmutableMolecularCriterium.builder()
                        .addGenes(testGene1)
                        .addGenes(testGene2)
                        .addFusions(testFusion)
                        .build())
                .build();


        List<EfficacyEvidence> efficacyEvidences = new ArrayList<>(List.of(evidence1, evidence2));
        efficacyEvidences.sort(new EfficacyEvidenceComparator());

        assertEquals(evidence1, efficacyEvidences.get(0));
        assertEquals(evidence2, efficacyEvidences.get(1));
    }

    @NotNull
    private static EfficacyEvidence create(@NotNull String applicableCancerType, @NotNull EvidenceLevel evidenceLevel,
            @NotNull EvidenceLevelDetails evidenceLevelDetails, @NotNull EvidenceDirection evidenceDirection) {
        return EfficacyEvidenceTestFactory.createTestEfficacyEvidence(Knowledgebase.UNKNOWN,
                applicableCancerType,
                Strings.EMPTY,
                evidenceLevel,
                evidenceLevelDetails,
                evidenceDirection,
                2024,
                Collections.emptySet());
    }
}