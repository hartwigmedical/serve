package com.hartwig.serve.datamodel.range;

import java.util.Objects;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class RangeTestFactory {

    private RangeTestFactory() {
    }

    @NotNull
    public static RangeAnnotation createRangeAnnotation(@NotNull String gene, @NotNull GeneRole geneRole,
            @NotNull ProteinEffect proteinEffect, @NotNull String chromosome, int start, int end, @NotNull String transcript, int rank,
            @NotNull MutationType applicableMutationType) {
        return new RangeAnnotationImpl(gene, geneRole, proteinEffect, chromosome, start, end, transcript, rank, applicableMutationType);
    }

    @NotNull
    public static ImmutableCodonAnnotation.Builder codonAnnotationBuilder() {
        return ImmutableCodonAnnotation.builder()
                .gene(Strings.EMPTY)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .chromosome(Strings.EMPTY)
                .start(0)
                .end(0)
                .transcript(Strings.EMPTY)
                .rank(0)
                .applicableMutationType(MutationType.ANY);
    }

    @NotNull
    public static CodonAnnotation createTestCodonAnnotation() {
        return codonAnnotationBuilder().build();
    }

    @NotNull
    public static KnownCodon createTestKnownCodonForSource(@NotNull Knowledgebase source) {
        return ImmutableKnownCodon.builder().from(createTestKnownCodon()).addSources(source).build();
    }

    @NotNull
    public static KnownCodon createTestKnownCodon() {
        return ImmutableKnownCodon.builder().annotation(createTestCodonAnnotation()).build();
    }

    @NotNull
    public static ImmutableExonAnnotation.Builder exonAnnotationBuilder() {
        return ImmutableExonAnnotation.builder()
                .gene(Strings.EMPTY)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .chromosome(Strings.EMPTY)
                .start(0)
                .end(0)
                .transcript(Strings.EMPTY)
                .rank(0)
                .applicableMutationType(MutationType.ANY);
    }

    @NotNull
    public static ExonAnnotation createTestExonAnnotation() {
        return exonAnnotationBuilder().build();
    }

    @NotNull
    public static KnownExon createTestKnownExonForSource(@NotNull Knowledgebase source) {
        return ImmutableKnownExon.builder().from(createTestKnownExon()).addSources(source).build();
    }

    @NotNull
    public static KnownExon createTestKnownExon() {
        return ImmutableKnownExon.builder().annotation(createTestExonAnnotation()).build();
    }

    @NotNull
    public static ImmutableActionableRange.Builder actionableRangeBuilder() {
        return ImmutableActionableRange.builder()
                .from(DatamodelTestFactory.createEmptyActionableEvent())
                .gene(Strings.EMPTY)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .chromosome(Strings.EMPTY)
                .start(0)
                .end(0)
                .transcript(Strings.EMPTY)
                .rank(0)
                .applicableMutationType(MutationType.ANY)
                .rangeType(RangeType.EXON);
    }

    @NotNull
    public static ActionableRange createTestActionableRangeForSource(@NotNull Knowledgebase source) {
        return actionableRangeBuilder().source(source).build();
    }

    private static class RangeAnnotationImpl implements RangeAnnotation {

        @NotNull
        private final String gene;
        @NotNull
        private final GeneRole geneRole;
        @NotNull
        private final ProteinEffect proteinEffect;
        @NotNull
        private final String chromosome;
        private final int start;
        private final int end;
        @NotNull
        private final String transcript;
        private final int rank;
        @NotNull
        private final MutationType applicableMutationType;

        public RangeAnnotationImpl(@NotNull final String gene, @NotNull final GeneRole geneRole, @NotNull final ProteinEffect proteinEffect,
                @NotNull final String chromosome, final int start, final int end, @NotNull final String transcript, final int rank,
                @NotNull final MutationType applicableMutationType) {
            this.gene = gene;
            this.geneRole = geneRole;
            this.proteinEffect = proteinEffect;
            this.chromosome = chromosome;
            this.start = start;
            this.end = end;
            this.transcript = transcript;
            this.rank = rank;
            this.applicableMutationType = applicableMutationType;
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

        @NotNull
        @Override
        public String transcript() {
            return transcript;
        }

        @Override
        public int rank() {
            return rank;
        }

        @NotNull
        @Override
        public MutationType applicableMutationType() {
            return applicableMutationType;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final RangeAnnotationImpl that = (RangeAnnotationImpl) o;
            return start == that.start && end == that.end && rank == that.rank && gene.equals(that.gene) && geneRole == that.geneRole
                    && proteinEffect == that.proteinEffect && chromosome.equals(that.chromosome) && transcript.equals(that.transcript)
                    && applicableMutationType == that.applicableMutationType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(gene, geneRole, proteinEffect, chromosome, start, end, transcript, rank, applicableMutationType);
        }

        @Override
        public String toString() {
            return "RangeAnnotationImpl{" + "gene='" + gene + '\'' + ", geneRole=" + geneRole + ", proteinEffect=" + proteinEffect
                    + ", chromosome='" + chromosome + '\'' + ", start=" + start + ", end=" + end + ", transcript='" + transcript + '\''
                    + ", rank=" + rank + ", applicableMutationType=" + applicableMutationType + '}';
        }
    }
}
