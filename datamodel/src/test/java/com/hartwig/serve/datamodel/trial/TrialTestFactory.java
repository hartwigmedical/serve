package com.hartwig.serve.datamodel.trial;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TrialTestFactory {

    private TrialTestFactory() {
    }

    @NotNull
    public static ImmutableActionableTrial.Builder builder() {
        return ImmutableActionableTrial.builder()
                .source(Knowledgebase.UNKNOWN)
                .nctId(Strings.EMPTY)
                .title(Strings.EMPTY)
                .countries(Collections.emptySet())
                .therapyNames(Collections.emptySet())
                .genderCriterium(GenderCriterium.BOTH)
                .indications(Collections.emptySet())
                .anyMolecularCriteria(Collections.emptySet())
                .urls(Collections.emptySet());
    }

    @NotNull
    public static ActionableTrial createWithMolecularCriterium(@NotNull MolecularCriterium molecularCriterium) {
        return builder().addAnyMolecularCriteria(molecularCriterium).build();
    }

    @NotNull
    public static ImmutableCountry.Builder countryBuilder() {
        return ImmutableCountry.builder().name(Strings.EMPTY).hospitalsPerCity(new HashMap<>());
    }

    @NotNull
    public static Country createTestCountry(@NotNull String name, @NotNull String city) {
        return countryBuilder().name(name).hospitalsPerCity(Map.of(city, Set.of(createTestHospital("hospital 1")))).build();
    }

    @NotNull
    public static ImmutableHospital.Builder hospitalBuilder() {
        return ImmutableHospital.builder().name(Strings.EMPTY);
    }

    @NotNull
    public static Hospital createTestHospital(@NotNull String name) {
        return hospitalBuilder().name(name).build();
    }
}
