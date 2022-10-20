package com.hartwig.serve.datamodel.genome;

import com.hartwig.serve.datamodel.genome.chromosome.ContigComparator;

import org.jetbrains.annotations.NotNull;

public interface GenomeRegion extends Comparable<GenomeRegion> {

    @NotNull
    String chromosome();

    int start();

    int end();

    @Override
    default int compareTo(@NotNull final GenomeRegion other) {
        if (chromosome().equals(other.chromosome())) {
            if (start() < other.start()) {
                return -1;
            } else if (start() == other.start()) {
                return 0;
            }
            return 1;
        }

        return new ContigComparator().compare(chromosome(), other.chromosome());
    }
}
