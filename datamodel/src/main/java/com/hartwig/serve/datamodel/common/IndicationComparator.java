package com.hartwig.serve.datamodel.common;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class IndicationComparator implements Comparator<Indication> {

    @NotNull
    private final Comparator<CancerType> cancerTypeComparator = new CancerTypeComparator();

    @Override
    public int compare(@NotNull Indication indication1, @NotNull Indication indication2) {
        int applicableTypeCompare = cancerTypeComparator.compare(indication1.applicableType(), indication2.applicableType());
        if (applicableTypeCompare != 0) {
            return applicableTypeCompare;
        }

        return CompareFunctions.compareSetOfComparable(indication1.excludedSubTypes(), indication2.excludedSubTypes());
    }
}
