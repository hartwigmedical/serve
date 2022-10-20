package com.hartwig.serve.datamodel.genome.chromosome;

import java.util.Comparator;

public class ContigComparator implements Comparator<String> {

    @Override
    public int compare(final String contig1, final String contig2) {
        int rank1 = HumanChromosome.chromosomeRank(contig1);
        int rank2 = HumanChromosome.chromosomeRank(contig2);

        if (rank1 == rank2) {
            return 0;
        }

        return rank1 < rank2 ? -1 : 1;
    }
}
