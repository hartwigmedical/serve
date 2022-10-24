package com.hartwig.serve.datamodel.genome;

import java.util.Comparator;

import com.hartwig.serve.datamodel.genome.chromosome.ContigComparator;

import org.jetbrains.annotations.NotNull;

public class GenomeRegionComparator implements Comparator<GenomeRegion> {

    @NotNull
    private final ContigComparator contigComparator = new ContigComparator();

    @Override
    public int compare(@NotNull GenomeRegion region1, @NotNull GenomeRegion region2) {
        int chromosomeCompare = contigComparator.compare(region1.chromosome(), region2.chromosome());
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
