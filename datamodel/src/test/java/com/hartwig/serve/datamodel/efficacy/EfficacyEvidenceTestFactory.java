package com.hartwig.serve.datamodel.efficacy;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.DatamodelTestFactory;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriteriumTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class EfficacyEvidenceTestFactory {

    private EfficacyEvidenceTestFactory() {
    }

    @NotNull
    public static ImmutableEfficacyEvidence.Builder builder() {
        return ImmutableEfficacyEvidence.builder()
                .source(Knowledgebase.UNKNOWN)
                .treatment(DatamodelTestFactory.treatmentBuilder().build())
                .indication(DatamodelTestFactory.indicationBuilder().build())
                .molecularCriterium(MolecularCriteriumTestFactory.createWithTestActionableHotspot())
                .efficacyDescription(Strings.EMPTY)
                .evidenceLevel(EvidenceLevel.A)
                .evidenceLevelDetails(EvidenceLevelDetails.FDA_APPROVED)
                .evidenceDirection(EvidenceDirection.RESPONSIVE)
                .evidenceYear(2024)
                .urls(Sets.newHashSet());
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
                .indication(DatamodelTestFactory.createTestIndication(applicableCancerType, Strings.EMPTY))
                .efficacyDescription(efficacyDescription)
                .evidenceLevel(evidenceLevel)
                .evidenceLevelDetails(evidenceLevelDetails)
                .evidenceDirection(evidenceDirection)
                .evidenceYear(evidenceYear)
                .urls(urls)
                .build();
    }
}
