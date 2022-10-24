package com.hartwig.serve.datamodel.common;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class GenomePositionComparator implements Comparator<GenomePosition> {

    @NotNull
    private final ChromosomeComparator chromosomeComparator = new ChromosomeComparator();

    @Override
    public int compare(@NotNull GenomePosition position1, @NotNull GenomePosition position2) {
        int chromosomeCompare = chromosomeComparator.compare(position1.chromosome(), position2.chromosome());
        if (chromosomeCompare != 0) {
            return chromosomeCompare;
        }

        return Integer.compare(position1.position(), position2.position());
    }
}
