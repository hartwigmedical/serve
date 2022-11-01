package com.hartwig.serve.datamodel.hotspot;

import java.util.Comparator;

import com.hartwig.serve.datamodel.common.GenomePosition;
import com.hartwig.serve.datamodel.common.GenomePositionComparator;

import org.jetbrains.annotations.NotNull;

public class VariantHotspotComparator implements Comparator<VariantHotspot> {

    @NotNull
    private final Comparator<GenomePosition> genomePositionComparator = new GenomePositionComparator();

    @Override
    public int compare(@NotNull VariantHotspot hotspot1, @NotNull VariantHotspot hotspot2) {
        int genomePositionCompare = genomePositionComparator.compare(hotspot1, hotspot2);
        if (genomePositionCompare != 0) {
            return genomePositionCompare;
        }

        int o1Length = Math.max(hotspot1.ref().length(), hotspot1.alt().length());
        int o2Length = Math.max(hotspot2.ref().length(), hotspot2.alt().length());
        int lengthCompare = Integer.compare(o1Length, o2Length);
        if (lengthCompare != 0) {
            return lengthCompare;
        }

        int refCompare = hotspot1.ref().compareTo(hotspot2.ref());
        if (refCompare != 0) {
            return refCompare;
        }

        int altCompare = hotspot1.alt().compareTo(hotspot2.alt());
        if (altCompare != 0) {
            return altCompare;
        }

        return hotspot1.gene().compareTo(hotspot2.gene());
    }
}
