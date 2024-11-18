package com.hartwig.serve.datamodel.common;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class CancerTypeComparator implements Comparator<CancerType> {

    @Override
    public int compare(@NotNull CancerType cancerType1, @NotNull CancerType cancerType2) {
        int nameCompare = cancerType1.name().compareTo(cancerType2.name());
        if (nameCompare != 0) {
            return nameCompare;
        }

        return cancerType1.doid().compareTo(cancerType2.doid());
    }
}
