package com.hartwig.serve.datamodel.common;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class VariantComparator implements Comparator<Variant> {

    @NotNull
    private final Comparator<GenomePosition> genomePositionComparator = new GenomePositionComparator();

    @Override
    public int compare(@NotNull Variant variant1, @NotNull Variant variant2) {
        int genomePositionCompare = genomePositionComparator.compare(variant1, variant2);
        if (genomePositionCompare != 0) {
            return genomePositionCompare;
        }

        int o1Length = Math.max(variant1.ref().length(), variant1.alt().length());
        int o2Length = Math.max(variant2.ref().length(), variant2.alt().length());
        int lengthCompare = Integer.compare(o1Length, o2Length);
        if (lengthCompare != 0) {
            return lengthCompare;
        }

        int refCompare = variant1.ref().compareTo(variant2.ref());
        if (refCompare != 0) {
            return refCompare;
        }

        return variant1.alt().compareTo(variant2.alt());
    }
}
