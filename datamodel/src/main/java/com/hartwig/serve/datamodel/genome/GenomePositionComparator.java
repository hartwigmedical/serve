package com.hartwig.serve.datamodel.genome;

import java.util.Comparator;

import com.hartwig.serve.datamodel.genome.chromosome.ContigComparator;

import org.jetbrains.annotations.NotNull;

public class GenomePositionComparator implements Comparator<GenomePosition> {

    @NotNull
    private final ContigComparator contigComparator = new ContigComparator();

    @Override
    public int compare(@NotNull GenomePosition position1, @NotNull GenomePosition position2) {
        int chromosomeCompare = contigComparator.compare(position1.chromosome(), position2.chromosome());
        if (chromosomeCompare != 0) {
            return chromosomeCompare;
        }

        return Integer.compare(position1.position(), position2.position());
    }
}
