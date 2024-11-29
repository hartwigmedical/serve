package com.hartwig.serve.datamodel.efficacy;

import java.util.Collections;
import java.util.Set;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.DatamodelTestFactory;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriteriumTestFactory;

import org.jetbrains.annotations.NotNull;

public final class EfficacyEvidenceTestFactory {

    private EfficacyEvidenceTestFactory() {
    }

    @NotNull
    public static ImmutableEfficacyEvidence.Builder builder() {
        return ImmutableEfficacyEvidence.builder()
                .source(Knowledgebase.UNKNOWN)
                .treatment(createTestTreatment())
                .indication(DatamodelTestFactory.indicationBuilder().build())
                .molecularCriterium(MolecularCriteriumTestFactory.createWithTestActionableHotspot())
                .efficacyDescription("")
                .evidenceLevel(EvidenceLevel.A)
                .evidenceLevelDetails(EvidenceLevelDetails.FDA_APPROVED)
                .evidenceDirection(EvidenceDirection.RESPONSIVE)
                .evidenceYear(2024)
                .urls(Collections.emptySet());
    }

    @NotNull
    public static EfficacyEvidence createWithMolecularCriterium(@NotNull MolecularCriterium molecularCriterium) {
        return builder().molecularCriterium(molecularCriterium).build();
    }

    @NotNull
    public static EfficacyEvidence createTestEfficacyEvidence(@NotNull Knowledgebase source, @NotNull String applicableCancerType,
            @NotNull String efficacyDescription, @NotNull EvidenceLevel evidenceLevel, @NotNull EvidenceLevelDetails evidenceLevelDetails,
            @NotNull EvidenceDirection evidenceDirection, @NotNull Integer evidenceYear, @NotNull Set<String> urls) {
        return builder().source(source)
                .indication(DatamodelTestFactory.createTestIndication(applicableCancerType, ""))
                .efficacyDescription(efficacyDescription)
                .evidenceLevel(evidenceLevel)
                .evidenceLevelDetails(evidenceLevelDetails)
                .evidenceDirection(evidenceDirection)
                .evidenceYear(evidenceYear)
                .urls(urls)
                .build();
    }

    @NotNull
    private static Treatment createTestTreatment() {
        return ImmutableTreatment.builder().name("").build();
    }
}
