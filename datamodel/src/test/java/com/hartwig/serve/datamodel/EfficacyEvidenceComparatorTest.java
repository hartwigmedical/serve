package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class EfficacyEvidenceComparatorTest {

    @Test
    public void canSortEfficacyEvidences() {
        EfficacyEvidence evidence1 = create("CancerA", EvidenceLevel.A, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESISTANT);
        EfficacyEvidence evidence2 = create("CancerA", EvidenceLevel.A, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESPONSIVE);
        EfficacyEvidence evidence3 = create("CancerA", EvidenceLevel.B, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESPONSIVE);
        EfficacyEvidence evidence4 = create("CancerB", EvidenceLevel.A, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESPONSIVE);

        List<EfficacyEvidence> efficacyEvidences = Lists.newArrayList(evidence4, evidence1, evidence3, evidence2);
        efficacyEvidences.sort(new EfficacyEvidenceComparator());

        assertEquals(evidence1, efficacyEvidences.get(0));
        assertEquals(evidence2, efficacyEvidences.get(1));
        assertEquals(evidence3, efficacyEvidences.get(2));
        assertEquals(evidence4, efficacyEvidences.get(3));
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
                Sets.newHashSet());
    }
}