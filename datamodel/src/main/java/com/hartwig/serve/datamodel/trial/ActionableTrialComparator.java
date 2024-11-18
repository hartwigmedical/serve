package com.hartwig.serve.datamodel.trial;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class ActionableTrialComparator implements Comparator<ActionableTrial> {

    @Override
    public int compare(@NotNull ActionableTrial actionableTrial1, @NotNull ActionableTrial actionableTrial2) {
        int sourceCompare = actionableTrial1.source().compareTo(actionableTrial2.source());
        if (sourceCompare != 0) {
            return sourceCompare;
        }

        int nctIdCompare = actionableTrial1.nctId().compareTo(actionableTrial2.nctId());
        if (nctIdCompare != 0) {
            return nctIdCompare;
        }

        int titleCompare = actionableTrial1.title().compareTo(actionableTrial2.title());
        if (titleCompare != 0) {
            return titleCompare;
        }

        int acronymCompare = CompareFunctions.compareNullableStrings(actionableTrial1.acronym(), actionableTrial2.acronym());
        if (acronymCompare != 0) {
            return acronymCompare;
        }

        int countriesCompare = CompareFunctions.compareSetOfComparable(actionableTrial1.countries(), actionableTrial2.countries());
        if (countriesCompare != 0) {
            return countriesCompare;
        }

        int therapyNamesCompare = CompareFunctions.compareSetOfComparable(actionableTrial1.therapyNames(), actionableTrial2.therapyNames());
        if (therapyNamesCompare != 0) {
            return therapyNamesCompare;
        }

        int genderCriteriumCompare = actionableTrial1.genderCriterium().compareTo(actionableTrial2.genderCriterium());
        if (genderCriteriumCompare != 0) {
            return genderCriteriumCompare;
        }

        int indicationsCompare = CompareFunctions.compareSetOfComparable(actionableTrial1.indications(), actionableTrial2.indications());
        if (indicationsCompare != 0) {
            return indicationsCompare;
        }

        int molecularCompare =
                CompareFunctions.compareSetOfComparable(actionableTrial1.anyMolecularCriteria(), actionableTrial2.anyMolecularCriteria());
        if (molecularCompare != 0) {
            return molecularCompare;
        }

        return CompareFunctions.compareSetOfComparable(actionableTrial1.urls(), actionableTrial2.urls());
    }
}