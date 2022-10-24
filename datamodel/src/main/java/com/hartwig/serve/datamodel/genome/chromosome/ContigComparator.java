package com.hartwig.serve.datamodel.genome.chromosome;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class ContigComparator implements Comparator<String> {

    @Override
    public int compare(@NotNull String contig1, @NotNull String contig2) {
        int rank1 = HumanChromosome.chromosomeRank(contig1);
        int rank2 = HumanChromosome.chromosomeRank(contig2);

        if (rank1 == rank2) {
            return 0;
        }

        return rank1 < rank2 ? -1 : 1;
    }
}
