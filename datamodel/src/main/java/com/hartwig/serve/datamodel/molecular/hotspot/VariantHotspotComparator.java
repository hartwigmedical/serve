package com.hartwig.serve.datamodel.molecular.hotspot;

import java.util.Comparator;

import com.hartwig.serve.datamodel.molecular.common.GenomePosition;
import com.hartwig.serve.datamodel.molecular.common.GenomePositionComparator;

import org.jetbrains.annotations.NotNull;

public class VariantHotspotComparator implements Comparator<VariantHotspot> {

    @NotNull
    private final Comparator<GenomePosition> genomePositionComparator = new GenomePositionComparator();

    @Override
    public int compare(@NotNull VariantHotspot variant1, @NotNull VariantHotspot variant2) {
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

        int altCompare = variant1.alt().compareTo(variant2.alt());
        if (altCompare != 0) {
            return altCompare;
        }

        return variant1.gene().compareTo(variant2.gene());
    }
}
