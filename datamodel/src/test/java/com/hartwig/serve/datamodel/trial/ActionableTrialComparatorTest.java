package com.hartwig.serve.datamodel.trial;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableTrialComparatorTest {

    @Test
    public void canSortActionableTrials() {
        ActionableTrial actionableTrial1 = create(Knowledgebase.DOCM, "nct1", "Belgium", "therapy 1", GenderCriterium.FEMALE);
        ActionableTrial actionableTrial2 = create(Knowledgebase.DOCM, "nct2", "Belgium", "therapy 1", GenderCriterium.FEMALE);
        ActionableTrial actionableTrial3 = create(Knowledgebase.DOCM, "nct2", "Netherlands", "therapy 1", GenderCriterium.FEMALE);
        ActionableTrial actionableTrial4 = create(Knowledgebase.VICC_CGI, "nct1", "Belgium", "therapy 1", GenderCriterium.FEMALE);

        List<ActionableTrial> actionableTrials = Lists.newArrayList(actionableTrial3, actionableTrial1, actionableTrial4, actionableTrial2);
        actionableTrials.sort(new ActionableTrialComparator());

        assertEquals(actionableTrial1, actionableTrials.get(0));
        assertEquals(actionableTrial2, actionableTrials.get(1));
        assertEquals(actionableTrial3, actionableTrials.get(2));
        assertEquals(actionableTrial4, actionableTrials.get(3));
    }

    @NotNull
    private static ActionableTrial create(@NotNull Knowledgebase source, @NotNull String nctId, @NotNull String countryName,
            @NotNull String therapy, @NotNull GenderCriterium genderCriterium) {
        return TrialTestFactory.builder()
                .source(source)
                .nctId(nctId)
                .countries(Set.of(TrialTestFactory.createTestCountry(countryName)))
                .therapyNames(Set.of(therapy))
                .genderCriterium(genderCriterium)
                .build();
    }
}