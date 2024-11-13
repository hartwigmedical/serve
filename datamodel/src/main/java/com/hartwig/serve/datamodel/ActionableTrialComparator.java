package com.hartwig.serve.datamodel;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

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

        int compareCountries = compareCountries(actionableTrial1.countries(), actionableTrial2.countries());
        if (compareCountries != 0) {
            return compareCountries;
        }

        int compareTherapyNames = CompareFunctions.compareSetOfComparable(actionableTrial1.therapyNames(), actionableTrial2.therapyNames());
        if (compareTherapyNames != 0) {
            return compareTherapyNames;
        }

        int genderCriteriumCompare = actionableTrial1.genderCriterium().compareTo(actionableTrial2.genderCriterium());
        if (genderCriteriumCompare != 0) {
            return genderCriteriumCompare;
        }

        return compareIndications(actionableTrial1.indications(), actionableTrial2.indications());
    }

    private static int compareCountries(@NotNull Set<Country> set1, @NotNull Set<Country> set2) {
        Iterator<Country> iterator1 = set1.iterator();
        Iterator<Country> iterator2 = set2.iterator();

        while (iterator1.hasNext() && iterator2.hasNext()) {
            int compare = iterator1.next().countryName().compareTo(iterator2.next().countryName());
            if (compare != 0) {
                return compare;
            }
        }

        return Integer.compare(set1.size(), set2.size());
    }

    private static int compareIndications(@NotNull Set<Indication> indication1, @NotNull Set<Indication> indication2) {
        Iterator<Indication> iterator1 = indication1.iterator();
        Iterator<Indication> iterator2 = indication2.iterator();

        while (iterator1.hasNext() && iterator2.hasNext()) {
            int compare = iterator1.next().applicableType().name().compareTo(iterator2.next().applicableType().name());
            if (compare != 0) {
                return compare;
            }
        }

        return Integer.compare(indication1.size(), indication2.size());
    }
}