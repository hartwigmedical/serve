package com.hartwig.serve.datamodel.range;

import java.util.Objects;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.MutationType;
import com.hartwig.serve.datamodel.common.CommonTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class RangeTestFactory {

    private RangeTestFactory() {
    }

    @NotNull
    public static RangeAnnotation createTestRangeAnnotation() {
        return createRangeAnnotation(Strings.EMPTY, "1", 0, 0, MutationType.ANY);
    }

    @NotNull
    public static RangeAnnotation createRangeAnnotation(@NotNull String gene, @NotNull String chromosome, int start, int end,
            @NotNull MutationType applicableMutationType) {
        return new RangeAnnotationImpl(gene, chromosome, start, end, applicableMutationType);
    }

    @NotNull
    public static ImmutableKnownCodon.Builder knownCodonBuilder() {
        return ImmutableKnownCodon.builder()
                .from(createTestRangeAnnotation())
                .from(CommonTestFactory.createTestGeneAlteration())
                .inputTranscript(Strings.EMPTY)
                .inputCodonRank(0);
    }

    @NotNull
    public static KnownCodon createTestKnownCodonForSource(@NotNull Knowledgebase source) {
        return knownCodonBuilder().addSources(source).build();
    }

    @NotNull
    public static ImmutableKnownExon.Builder knownExonBuilder() {
        return ImmutableKnownExon.builder()
                .from(createTestRangeAnnotation())
                .from(CommonTestFactory.createTestGeneAlteration())
                .inputTranscript(Strings.EMPTY)
                .inputExonRank(0);
    }

    @NotNull
    public static KnownExon createTestKnownExonForSource(@NotNull Knowledgebase source) {
        return knownExonBuilder().addSources(source).build();
    }

    @NotNull
    public static ActionableRange createTestActionableRangeForSource() {
        return actionableRangeBuilder().build();
    }

    @NotNull
    public static ImmutableActionableRange.Builder actionableRangeBuilder() {
        return ImmutableActionableRange.builder().from(createTestRangeAnnotation()).from(DatamodelTestFactory.createTestActionableEvent());
    }

    private static class RangeAnnotationImpl implements RangeAnnotation {

        @NotNull
        private final String gene;
        @NotNull
        private final String chromosome;
        private final int start;
        private final int end;
        @NotNull
        private final MutationType applicableMutationType;

        public RangeAnnotationImpl(@NotNull final String gene, @NotNull final String chromosome, final int start, final int end,
                @NotNull final MutationType applicableMutationType) {
            this.gene = gene;
            this.chromosome = chromosome;
            this.start = start;
            this.end = end;
            this.applicableMutationType = applicableMutationType;
        }

        @NotNull
        @Override
        public String gene() {
            return gene;
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
            return start == that.start && end == that.end && gene.equals(that.gene) && chromosome.equals(that.chromosome)
                    && applicableMutationType == that.applicableMutationType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(gene, chromosome, start, end, applicableMutationType);
        }

        @Override
        public String toString() {
            return "RangeAnnotationImpl{" + "gene='" + gene + '\'' + ", chromosome='" + chromosome + '\'' + ", start=" + start + ", end="
                    + end + ", applicableMutationType=" + applicableMutationType + '}';
        }
    }
}
