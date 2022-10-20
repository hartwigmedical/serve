package com.hartwig.serve.common.knownfusion;

import com.hartwig.serve.datamodel.genome.chromosome.HumanChromosome;

import org.jetbrains.annotations.NotNull;

public class ChrBaseRegion implements Cloneable, Comparable<ChrBaseRegion> {

    @NotNull
    private final String chromosome;
    private int[] positions;

    public ChrBaseRegion(@NotNull String chromosome, final int posStart, final int posEnd) {
        this.chromosome = chromosome;
        positions = new int[] { posStart, posEnd };
    }

    public int start() {
        return positions[StartEndIterator.SE_START];
    }

    public int end() {
        return positions[StartEndIterator.SE_END];
    }

    public boolean matches(final ChrBaseRegion other) {
        return chromosome.equals(other.chromosome) && start() == other.start() && end() == other.end();
    }

    public String toString() {
        return String.format("%s:%d-%d", chromosome, positions[StartEndIterator.SE_START], positions[StartEndIterator.SE_END]);
    }

    @Override
    public Object clone() {
        try {
            ChrBaseRegion br = (ChrBaseRegion) super.clone();
            br.positions = positions.clone();
            return br;
        } catch (CloneNotSupportedException e) {
            // Will not happen in this case
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        // same instance
        if (obj == this) {
            return true;
        }
        // null
        if (obj == null) {
            return false;
        }
        // type
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        // cast and compare state
        ChrBaseRegion other = (ChrBaseRegion) obj;
        return matches(other);
    }

    @Override
    public int compareTo(@NotNull final ChrBaseRegion other) {
        if (chromosome.equals(other.chromosome)) {
            if (start() < other.start()) {
                return -1;
            } else if (start() == other.start()) {
                return 0;
            }
            return 1;
        }

        int rank1 = HumanChromosome.chromosomeRank(chromosome);
        int rank2 = HumanChromosome.chromosomeRank(other.chromosome);

        if (rank1 == rank2) {
            return 0;
        }

        return rank1 < rank2 ? -1 : 1;
    }
}

