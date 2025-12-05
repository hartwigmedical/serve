package com.hartwig.serve.datamodel.trial;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.Knowledgebase;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableTrialComparatorTest {

    @Test
    public void canSortActionableTrials() {
        ActionableTrial actionableTrial1 =
                create(Knowledgebase.DOCM, "nct1", Phase.PHASE_I, "Belgium", "therapy 1", GenderCriterium.FEMALE);
        ActionableTrial actionableTrial2 =
                create(Knowledgebase.DOCM, "nct2", Phase.PHASE_I, "Belgium", "therapy 1", GenderCriterium.FEMALE);
        ActionableTrial actionableTrial3 =
                create(Knowledgebase.DOCM, "nct2", Phase.PHASE_I, "Netherlands", "therapy 1", GenderCriterium.FEMALE);
        ActionableTrial actionableTrial4 =
                create(Knowledgebase.DOCM, "nct2", Phase.PHASE_II, "Belgium", "therapy 1", GenderCriterium.FEMALE);
        ActionableTrial actionableTrial5 =
                create(Knowledgebase.VICC_CGI, "nct1", Phase.PHASE_I, "Belgium", "therapy 1", GenderCriterium.FEMALE);

        List<ActionableTrial> actionableTrials =
                new ArrayList<>(List.of(actionableTrial3, actionableTrial1, actionableTrial5, actionableTrial4, actionableTrial2));
        actionableTrials.sort(new ActionableTrialComparator());

        assertEquals(actionableTrial1, actionableTrials.get(0));
        assertEquals(actionableTrial2, actionableTrials.get(1));
        assertEquals(actionableTrial3, actionableTrials.get(2));
        assertEquals(actionableTrial4, actionableTrials.get(3));
        assertEquals(actionableTrial5, actionableTrials.get(4));
    }

    @NotNull
    private static ActionableTrial create(@NotNull Knowledgebase source, @NotNull String nctId, @NotNull Phase phase,
            @NotNull String countryName,
            @NotNull String therapy, @NotNull GenderCriterium genderCriterium) {
        return TrialTestFactory.builder()
                .source(source)
                .nctId(nctId)
                .phase(phase)
                .countries(Set.of(TrialTestFactory.createTestCountry(countryName, "city")))
                .therapyNames(Set.of(therapy))
                .genderCriterium(genderCriterium)
                .build();
    }
}