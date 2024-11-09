package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableTrialComparatorTest {

    @Test
    public void canSortActionableTrials() {
        ActionableTrial actionableTrial1 =
                create("nct1", "title 1", "Belgium", "therapy 1", GenderCriterium.FEMALE, "cancerType1", "excludedCancerSubType1");
        ActionableTrial actionableTrial2 =
                create("nct2", "title 2", "Netherlands", "therapy 2", GenderCriterium.BOTH, "cancerType2", "excludedCancerSubType2");
        ActionableTrial actionableTrial3 =
                create("nct3", "title 3", "Germany", "therapy 3", GenderCriterium.MALE, "cancerType3", "excludedCancerSubType3");
        ActionableTrial actionableTrial4 =
                create("nct4", "title 4", "Netherlands", "therapy 4", GenderCriterium.FEMALE, "cancerType4", "excludedCancerSubType4");

        List<ActionableTrial> actionableTrials = Lists.newArrayList(actionableTrial3, actionableTrial1, actionableTrial4, actionableTrial2);
        actionableTrials.sort(new ActionableTrialComparator());

        assertEquals(actionableTrial1, actionableTrials.get(0));
        assertEquals(actionableTrial2, actionableTrials.get(1));
        assertEquals(actionableTrial3, actionableTrials.get(2));
        assertEquals(actionableTrial4, actionableTrials.get(3));
    }

    @NotNull
    private static ActionableTrial create(@NotNull String nctId, @NotNull String title, @NotNull String countryName,
            @NotNull String therapy, @NotNull GenderCriterium genderCriterium, @NotNull String applicableCancerType,
            @NotNull String excludedCancerSubType) {
        return ActionableTrialTestFactory.createTestActionableTrial(nctId,
                title,
                countryName,
                Set.of(therapy),
                genderCriterium,
                applicableCancerType,
                excludedCancerSubType);
    }
}