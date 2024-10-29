package com.hartwig.serve.datamodel;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

public class ClinicalTrialComparator implements Comparator<ClinicalTrial> {

    @Override
    public int compare(@NotNull ClinicalTrial clinicalTrial1, @NotNull ClinicalTrial clinicalTrial2) {
        int nctIdCompare = clinicalTrial1.nctId().compareTo(clinicalTrial2.nctId());
        if (nctIdCompare != 0) {
            return nctIdCompare;
        }

        int titleCompare = clinicalTrial1.title().compareTo(clinicalTrial2.title());
        if (titleCompare != 0) {
            return titleCompare;
        }

        int compareCountries = compareCountries(clinicalTrial1.countries(), clinicalTrial2.countries());
        if (compareCountries != 0) {
            return compareCountries;
        }

        int compareTherapyNames = compareTherapyNames(clinicalTrial1.therapyNames(), clinicalTrial2.therapyNames());
        if (compareTherapyNames != 0) {
            return compareTherapyNames;
        }

        int genderCriteriumCompare = clinicalTrial1.genderCriterium().toString().compareTo(clinicalTrial2.genderCriterium().toString());
        if (genderCriteriumCompare != 0) {
            return genderCriteriumCompare;
        }

        return compareIndications(clinicalTrial1.indications(), clinicalTrial2.indications());
    }

    private static int compareTherapyNames(@NotNull Set<String> set1, @NotNull Set<String> set2) {
        Iterator<String> iterator1 = set1.iterator();
        Iterator<String> iterator2 = set2.iterator();

        while (iterator1.hasNext() && iterator2.hasNext()) {
            int compare = iterator1.next().compareTo(iterator2.next());
            if (compare != 0) {
                return compare;
            }
        }

        return Integer.compare(set1.size(), set2.size());
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
            int compare = iterator1.next().applicableCancerType().name().compareTo(iterator2.next().applicableCancerType().name());
            if (compare != 0) {
                return compare;
            }
        }

        return Integer.compare(indication1.size(), indication2.size());
    }
}