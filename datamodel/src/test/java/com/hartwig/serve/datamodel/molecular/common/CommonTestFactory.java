package com.hartwig.serve.datamodel.molecular.common;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public static GeneAlteration createTestGeneAlteration() {
        return createGeneAlteration(GeneRole.UNKNOWN, ProteinEffect.UNKNOWN, null);
    }

    @NotNull
    public static GeneAlteration createGeneAlteration(@NotNull GeneRole geneRole, @NotNull ProteinEffect proteinEffect,
            @Nullable Boolean associatedWithDrugResistance) {
        return new GeneAlterationImpl(geneRole, proteinEffect, associatedWithDrugResistance);
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
        private final GeneRole geneRole;
        @NotNull
        private final ProteinEffect proteinEffect;
        @Nullable
        private final Boolean associatedWithDrugResistance;

        public GeneAlterationImpl(@NotNull final GeneRole geneRole, @NotNull final ProteinEffect proteinEffect,
                @Nullable final Boolean associatedWithDrugResistance) {
            this.geneRole = geneRole;
            this.proteinEffect = proteinEffect;
            this.associatedWithDrugResistance = associatedWithDrugResistance;
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

        @Nullable
        @Override
        public Boolean associatedWithDrugResistance() {
            return associatedWithDrugResistance;
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
            return geneRole == that.geneRole && proteinEffect == that.proteinEffect && Objects.equals(associatedWithDrugResistance,
                    that.associatedWithDrugResistance);
        }

        @Override
        public int hashCode() {
            return Objects.hash(geneRole, proteinEffect, associatedWithDrugResistance);
        }

        @Override
        public String toString() {
            return "GeneAlterationImpl{" + "geneRole=" + geneRole + ", proteinEffect=" + proteinEffect + ", associatedWithDrugResistance="
                    + associatedWithDrugResistance + '}';
        }
    }
}
