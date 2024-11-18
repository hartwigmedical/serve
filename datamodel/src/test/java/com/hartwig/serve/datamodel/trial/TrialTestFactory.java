package com.hartwig.serve.datamodel.trial;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
                .countries(Sets.newHashSet())
                .therapyNames(Sets.newHashSet())
                .genderCriterium(GenderCriterium.BOTH)
                .indications(Sets.newHashSet())
                .anyMolecularCriteria(Sets.newHashSet())
                .urls(Sets.newHashSet());
    }

    @NotNull
    public static ActionableTrial createWithMolecularCriterium(@NotNull MolecularCriterium molecularCriterium) {
        return builder().addAnyMolecularCriteria(molecularCriterium).build();
    }

    @NotNull
    public static ImmutableCountry.Builder countryBuilder() {
        return ImmutableCountry.builder().name(Strings.EMPTY).hospitalsPerCity(Maps.newHashMap());
    }

    @NotNull
    public static Country createTestCountry(@NotNull String name, @NotNull String city) {
        return countryBuilder().name(name).hospitalsPerCity(Map.of(city, Set.of(createTestHospital()))).build();
    }

    @NotNull
    public static ImmutableHospital.Builder hospitalBuilder() {
        return ImmutableHospital.builder().name(Strings.EMPTY);
    }

    @NotNull
    private static Hospital createTestHospital() {
        return hospitalBuilder().name("hospital name").isChildrensHospital(false).build();
    }
}
