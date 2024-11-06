package com.hartwig.serve.datamodel;

import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class ClinicalTrialTestFactory {

    private ClinicalTrialTestFactory() {
    }

    @NotNull
    public static ImmutableClinicalTrial.Builder builder() {
        return ImmutableClinicalTrial.builder()
                .source(Knowledgebase.UNKNOWN)
                .nctId(Strings.EMPTY)
                .title(Strings.EMPTY)
                .countries(Sets.newHashSet())
                .therapyNames(Sets.newHashSet())
                .genderCriterium(GenderCriterium.BOTH)
                .indications(Sets.newHashSet())
                .molecularCriteria(Sets.newHashSet())
                .urls(Sets.newHashSet());
    }

    @NotNull
    public static ClinicalTrial createWithMolecularCriterium(@NotNull MolecularCriterium molecularCriterium) {
        return builder().addMolecularCriteria(molecularCriterium).build();
    }

    @NotNull
    public static ClinicalTrial createTestClinicalTrial(@NotNull String nctId, @NotNull String title, @NotNull String countryName,
            @NotNull Set<String> therapyNames, @NotNull GenderCriterium genderCriterium, @NotNull String applicableCancerType,
            @NotNull String excludedCancerSubType) {
        return builder().nctId(nctId)
                .title(title)
                .countries(Set.of(DatamodelTestFactory.createTestCountry(countryName)))
                .therapyNames(therapyNames)
                .genderCriterium(genderCriterium)
                .indications(Set.of(DatamodelTestFactory.createTestIndication(applicableCancerType, excludedCancerSubType)))
                .build();
    }
}
