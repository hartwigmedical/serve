package com.hartwig.serve.datamodel.common;

import java.util.Objects;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class CommonTestFactory {

    private CommonTestFactory() {
    }

    @NotNull
    public static GenomePosition createGenomePosition(@NotNull String chromosome, int position) {
        return new GenomePositionImpl(chromosome, position);
    }

    @NotNull
    public static GenomeRegion createGenomeRegion(@NotNull String chromosome, int start, int end) {
        return new GenomeRegionImpl(chromosome, start, end);
    }

    @NotNull
    public static GeneAlteration createEmptyGeneAlteration() {
        return createGeneAlteration(Strings.EMPTY, GeneRole.UNKNOWN, ProteinEffect.UNKNOWN);
    }

    @NotNull
    public static GeneAlteration createGeneAlteration(@NotNull String gene, @NotNull GeneRole geneRole,
            @NotNull ProteinEffect proteinEffect) {
        return new GeneAlterationImpl(gene, geneRole, proteinEffect);
    }

    private static class GenomePositionImpl implements GenomePosition {

        @NotNull
        private final String chromosome;
        private final int position;

        public GenomePositionImpl(@NotNull final String chromosome, final int position) {
            this.chromosome = chromosome;
            this.position = position;
        }

        @NotNull
        @Override
        public String chromosome() {
            return chromosome;
        }

        @Override
        public int position() {
            return position;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final GenomePositionImpl that = (GenomePositionImpl) o;
            return position == that.position && chromosome.equals(that.chromosome);
        }

        @Override
        public int hashCode() {
            return Objects.hash(chromosome, position);
        }

        @Override
        public String toString() {
            return "GenomePositionImpl{" + "chromosome='" + chromosome + '\'' + ", position=" + position + '}';
        }
    }

    private static class GenomeRegionImpl implements GenomeRegion {

        @NotNull
        private final String chromosome;
        private final int start;
        private final int end;

        public GenomeRegionImpl(@NotNull final String chromosome, final int start, final int end) {
            this.chromosome = chromosome;
            this.start = start;
            this.end = end;
        }

        @NotNull
        @Override
        public String chromosome() {
            return chromosome;
        }

        @Override
        public int start() {
            return start;
        }

        @Override
        public int end() {
            return end;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final GenomeRegionImpl that = (GenomeRegionImpl) o;
            return start == that.start && end == that.end && chromosome.equals(that.chromosome);
        }

        @Override
        public int hashCode() {
            return Objects.hash(chromosome, start, end);
        }

        @Override
        public String toString() {
            return "GenomeRegionImpl{" + "chromosome='" + chromosome + '\'' + ", start=" + start + ", end=" + end + '}';
        }
    }

    private static class GeneAlterationImpl implements GeneAlteration {

        @NotNull
        private final String gene;
        @NotNull
        private final GeneRole geneRole;
        @NotNull
        private final ProteinEffect proteinEffect;

        public GeneAlterationImpl(@NotNull final String gene, @NotNull final GeneRole geneRole,
                @NotNull final ProteinEffect proteinEffect) {
            this.gene = gene;
            this.geneRole = geneRole;
            this.proteinEffect = proteinEffect;
        }

        @NotNull
        @Override
        public String gene() {
            return gene;
        }

        @NotNull
        @Override
        public GeneRole geneRole() {
            return geneRole;
        }

        @NotNull
        @Override
        public ProteinEffect proteinEffect() {
            return proteinEffect;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final GeneAlterationImpl that = (GeneAlterationImpl) o;
            return gene.equals(that.gene) && geneRole == that.geneRole && proteinEffect == that.proteinEffect;
        }

        @Override
        public int hashCode() {
            return Objects.hash(gene, geneRole, proteinEffect);
        }

        @Override
        public String toString() {
            return "GeneAlterationImpl{" + "gene='" + gene + '\'' + ", geneRole=" + geneRole + ", proteinEffect=" + proteinEffect + '}';
        }
    }
}
