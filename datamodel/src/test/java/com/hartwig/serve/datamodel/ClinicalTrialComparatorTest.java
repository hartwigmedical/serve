package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ClinicalTrialComparatorTest {

    @Test
    public void canSortEfficacyEvidences() {
        ClinicalTrial clinicalTrial1 =
                create("nct1", "title1", "Belgium", Set.of("therapy1"), GenderCriterium.FEMALE, "cancerType1", "ignoredCancerType1");
        ClinicalTrial clinicalTrial2 =
                create("nct2", "title2", "Netherlands", Set.of("therapy2"), GenderCriterium.BOTH, "cancerType2", "ignoredCancerType2");
        ClinicalTrial clinicalTrial3 =
                create("nct3", "title3", "Germany", Set.of("therapy3"), GenderCriterium.MALE, "cancerType3", "ignoredCancerType3");
        ClinicalTrial clinicalTrial4 =
                create("nct4", "title4", "Netherlands", Set.of("therapy4"), GenderCriterium.FEMALE, "cancerType4", "ignoredCancerType4");

        List<ClinicalTrial> clinicalTrials = Lists.newArrayList(clinicalTrial3, clinicalTrial1, clinicalTrial4, clinicalTrial2);
        clinicalTrials.sort(new ClinicalTrialComparator());

        assertEquals(clinicalTrial1, clinicalTrials.get(0));
        assertEquals(clinicalTrial2, clinicalTrials.get(1));
        assertEquals(clinicalTrial3, clinicalTrials.get(2));
        assertEquals(clinicalTrial4, clinicalTrials.get(3));
    }

    @NotNull
    private static ClinicalTrial create(@NotNull String nctId, @NotNull String title, @NotNull String countryName,
            @NotNull Set<String> therapyNames, @NotNull GenderCriterium genderCriterium, @NotNull String applicableCancerType,
            @NotNull String ignoredCancerType) {
        return DatamodelTestFactory.createClinicalTrial(nctId,
                title,
                countryName,
                therapyNames,
                genderCriterium,
                applicableCancerType,
                ignoredCancerType);
    }
}