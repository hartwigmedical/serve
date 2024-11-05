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
        EfficacyEvidence event1 = create("CancerA", EvidenceLevel.A, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESISTANT);
        EfficacyEvidence event2 = create("CancerA", EvidenceLevel.A, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESPONSIVE);
        EfficacyEvidence event3 = create("CancerA", EvidenceLevel.A, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESPONSIVE);
        EfficacyEvidence event4 = create("CancerB", EvidenceLevel.A, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESPONSIVE);
        EfficacyEvidence event5 = create("CancerA", EvidenceLevel.B, EvidenceLevelDetails.CLINICAL_STUDY, EvidenceDirection.RESPONSIVE);

        List<EfficacyEvidence> efficacyEvidences = Lists.newArrayList(event3, event5, event1, event4, event2);
        efficacyEvidences.sort(new EfficacyEvidenceComparator());

        assertEquals(event1, efficacyEvidences.get(0));
        assertEquals(event2, efficacyEvidences.get(1));
        assertEquals(event3, efficacyEvidences.get(2));
        assertEquals(event4, efficacyEvidences.get(3));
        assertEquals(event5, efficacyEvidences.get(4));
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