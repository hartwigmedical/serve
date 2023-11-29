package com.hartwig.serve.datamodel.gene;

import java.util.Objects;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.CommonTestFactory;
import com.hartwig.serve.datamodel.common.GeneRole;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class GeneTestFactory {

    private GeneTestFactory() {
    }

    @NotNull
    public static GeneAnnotation createTestGeneAnnotation() {
        return createGeneAnnotation(Strings.EMPTY, GeneEvent.ANY_MUTATION);
    }

    @NotNull
    public static GeneAnnotation createGeneAnnotation(@NotNull String gene, @NotNull GeneEvent event) {
        return new GeneAnnotationImpl(gene, event);
    }

    @NotNull
    public static ImmutableKnownGene.Builder knownGeneBuilder() {
        return ImmutableKnownGene.builder().gene(Strings.EMPTY).geneRole(GeneRole.UNKNOWN);
    }

    @NotNull
    public static ImmutableKnownCopyNumber.Builder knownCopyNumberBuilder() {
        return ImmutableKnownCopyNumber.builder().from(CommonTestFactory.createTestGeneAlteration()).from(createTestGeneAnnotation());
    }

    @NotNull
    public static KnownCopyNumber createTestKnownCopyNumberForSource(@NotNull Knowledgebase source) {
        return knownCopyNumberBuilder().addSources(source).build();
    }

    @NotNull
    public static KnownGene createTestKnownGeneForSource(@NotNull Knowledgebase source) {
        return knownGeneBuilder().addSources(source).build();
    }

    @NotNull
    public static ImmutableActionableGene.Builder actionableGeneBuilder() {
        return ImmutableActionableGene.builder().from(DatamodelTestFactory.createTestActionableEvent()).from(createTestGeneAnnotation());
    }

    @NotNull
    public static ActionableGene createTestActionableGeneForSource(@NotNull Knowledgebase source) {
        return actionableGeneBuilder().source(source).build();
    }

    private static class GeneAnnotationImpl implements GeneAnnotation {

        @NotNull
        private final String gene;
        @NotNull
        private final GeneEvent event;

        public GeneAnnotationImpl(@NotNull final String gene, @NotNull final GeneEvent event) {
            this.gene = gene;
            this.event = event;
        }

        @NotNull
        @Override
        public String gene() {
            return gene;
        }

        @NotNull
        @Override
        public GeneEvent event() {
            return event;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final GeneAnnotationImpl that = (GeneAnnotationImpl) o;
            return gene.equals(that.gene) && event == that.event;
        }

        @Override
        public int hashCode() {
            return Objects.hash(gene, event);
        }

        @Override
        public String toString() {
            return "GeneAnnotationImpl{" + "gene='" + gene + '\'' + ", event=" + event + '}';
        }
    }
}
