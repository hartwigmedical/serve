package com.hartwig.serve.datamodel.efficacy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hartwig.serve.datamodel.Knowledgebase;

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

    @NotNull
    private static EfficacyEvidence create(@NotNull String applicableCancerType, @NotNull EvidenceLevel evidenceLevel,
            @NotNull EvidenceLevelDetails evidenceLevelDetails, @NotNull EvidenceDirection evidenceDirection) {
        return EfficacyEvidenceTestFactory.createTestEfficacyEvidence(
            Knowledgebase.UNKNOWN,
            applicableCancerType,
            Strings.EMPTY,
            evidenceLevel,
            evidenceLevelDetails,
            evidenceDirection,
            2024,
            Collections.emptySet()
        );
    }
}