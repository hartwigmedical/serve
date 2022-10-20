package com.hartwig.serve.common.knownfusion;

import com.hartwig.serve.datamodel.genome.chromosome.HumanChromosome;

import org.jetbrains.annotations.NotNull;

public class ChrBaseRegion implements Cloneable, Comparable<ChrBaseRegion> {

    public final String Chromosome;
    public int[] Positions;

    public ChrBaseRegion(final String chromosome, final int posStart, final int posEnd) {
        Chromosome = chromosome;
        Positions = new int[] { posStart, posEnd };
    }

    public int start() {
        return Positions[StartEndIterator.SE_START];
    }

    public int end() {
        return Positions[StartEndIterator.SE_END];
    }

    public boolean matches(final ChrBaseRegion other) {
        return Chromosome.equals(other.Chromosome) && start() == other.start() && end() == other.end();
    }

    public String toString() {
        return String.format("%s:%d-%d", Chromosome, Positions[StartEndIterator.SE_START], Positions[StartEndIterator.SE_END]);
    }

    @Override
    public Object clone() {
        try {
            ChrBaseRegion br = (ChrBaseRegion) super.clone();
            br.Positions = Positions.clone();
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
        if (Chromosome.equals(other.Chromosome)) {
            if (start() < other.start()) {
                return -1;
            } else if (start() == other.start()) {
                return 0;
            }
            return 1;
        }

        int rank1 = HumanChromosome.chromosomeRank(Chromosome);
        int rank2 = HumanChromosome.chromosomeRank(other.Chromosome);

        if (rank1 == rank2) {
            return 0;
        }

        return rank1 < rank2 ? -1 : 1;
    }
}

