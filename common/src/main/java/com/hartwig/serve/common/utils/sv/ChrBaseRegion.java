package com.hartwig.serve.common.utils.sv;

import com.hartwig.serve.datamodel.genome.chromosome.HumanChromosome;
import com.hartwig.serve.datamodel.genome.region.GenomeRegion;

import org.jetbrains.annotations.NotNull;

public class ChrBaseRegion implements Cloneable, Comparable<ChrBaseRegion> {

    public final String Chromosome;
    public int[] Positions;

    public ChrBaseRegion(final String chromosome, final int posStart, final int posEnd) {
        Chromosome = chromosome;
        Positions = new int[] { posStart, posEnd };
    }

    public static ChrBaseRegion from(final GenomeRegion region) {
        return new ChrBaseRegion(region.chromosome(), region.start(), region.end());
    }

    public int start() {
        return Positions[StartEndIterator.SE_START];
    }

    public int end() {
        return Positions[StartEndIterator.SE_END];
    }

    public String chromosome() {
        return Chromosome;
    }

    public void setPosition(int position, int index) {
        Positions[index] = position;
    }

    public void setStart(int pos) {
        setPosition(pos, StartEndIterator.SE_START);
    }

    public void setEnd(int pos) {
        setPosition(pos, StartEndIterator.SE_END);
    }

    public int length() {
        return Positions[StartEndIterator.SE_END] - Positions[StartEndIterator.SE_START];
    }

    public boolean isValid() {
        return HumanChromosome.contains(Chromosome) && hasValidPositions();
    }

    public boolean hasValidPositions() {
        return Positions[StartEndIterator.SE_START] > 0 & Positions[StartEndIterator.SE_END] >= Positions[StartEndIterator.SE_START];
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

