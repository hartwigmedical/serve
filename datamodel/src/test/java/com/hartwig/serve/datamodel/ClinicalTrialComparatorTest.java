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
                create("nct1", "title 1", "Belgium", "therapy 1", GenderCriterium.FEMALE, "cancerType1", "excludedCancerSubType1");
        ClinicalTrial clinicalTrial2 =
                create("nct2", "title 2", "Netherlands", "therapy 2", GenderCriterium.BOTH, "cancerType2", "excludedCancerSubType2");
        ClinicalTrial clinicalTrial3 =
                create("nct3", "title 3", "Germany", "therapy 3", GenderCriterium.MALE, "cancerType3", "excludedCancerSubType3");
        ClinicalTrial clinicalTrial4 =
                create("nct4", "title 4", "Netherlands", "therapy 4", GenderCriterium.FEMALE, "cancerType4", "excludedCancerSubType4");

        List<ClinicalTrial> clinicalTrials = Lists.newArrayList(clinicalTrial3, clinicalTrial1, clinicalTrial4, clinicalTrial2);
        clinicalTrials.sort(new ClinicalTrialComparator());

        assertEquals(clinicalTrial1, clinicalTrials.get(0));
        assertEquals(clinicalTrial2, clinicalTrials.get(1));
        assertEquals(clinicalTrial3, clinicalTrials.get(2));
        assertEquals(clinicalTrial4, clinicalTrials.get(3));
    }

    @NotNull
    private static ClinicalTrial create(@NotNull String nctId, @NotNull String title, @NotNull String countryName, @NotNull String therapy,
            @NotNull GenderCriterium genderCriterium, @NotNull String applicableCancerType, @NotNull String excludedCancerSubType) {
        return ClinicalTrialTestFactory.createTestClinicalTrial(nctId,
                title,
                countryName,
                Set.of(therapy),
                genderCriterium,
                applicableCancerType,
                excludedCancerSubType);
    }
}