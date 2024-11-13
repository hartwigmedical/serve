package com.hartwig.serve.datamodel.molecular.common;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class GenomeRegionComparator implements Comparator<GenomeRegion> {

    @NotNull
    private final ChromosomeComparator chromosomeComparator = new ChromosomeComparator();

    @Override
    public int compare(@NotNull GenomeRegion region1, @NotNull GenomeRegion region2) {
        int chromosomeCompare = chromosomeComparator.compare(region1.chromosome(), region2.chromosome());
        if (chromosomeCompare != 0) {
            return chromosomeCompare;
        }

        int startCompare = Integer.compare(region1.start(), region2.start());
        if (startCompare != 0) {
            return startCompare;
        }

        return Integer.compare(region1.end(), region2.end());
    }
}
