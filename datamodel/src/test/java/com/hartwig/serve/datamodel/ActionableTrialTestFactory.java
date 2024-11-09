package com.hartwig.serve.datamodel;

import com.google.common.collect.Sets;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class ActionableTrialTestFactory {

    private ActionableTrialTestFactory() {
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
}
