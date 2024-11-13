package com.hartwig.serve.datamodel.molecular.hotspot;

import java.util.Objects;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.DatamodelTestFactory;
import com.hartwig.serve.datamodel.molecular.common.CommonTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class HotspotTestFactory {

    private HotspotTestFactory() {
    }

    @NotNull
    public static VariantHotspot createTestVariantHotspot() {
        return createVariantHotspot(Strings.EMPTY, "1", 0, Strings.EMPTY, Strings.EMPTY);
    }

    @NotNull
    public static VariantHotspot createVariantHotspot(@NotNull String gene, @NotNull String chromosome, int position, @NotNull String ref,
            @NotNull String alt) {
        return new VariantHotspotImpl(gene, chromosome, position, ref, alt);
    }

    @NotNull
    public static ImmutableKnownHotspot.Builder knownHotspotBuilder() {
        return ImmutableKnownHotspot.builder()
                .from(createTestVariantHotspot())
                .from(CommonTestFactory.createTestGeneAlteration())
                .inputTranscript(null)
                .inputProteinAnnotation(Strings.EMPTY);
    }

    @NotNull
    public static KnownHotspot createTestKnownHotspotForSource(@NotNull Knowledgebase source) {
        return knownHotspotBuilder().addSources(source).build();
    }

    @NotNull
    public static ImmutableActionableHotspot.Builder actionableHotspotBuilder() {
        return ImmutableActionableHotspot.builder().from(DatamodelTestFactory.createTestActionableEvent()).from(createTestVariantHotspot());
    }

    @NotNull
    public static ActionableHotspot createTestActionableHotspot() {
        return actionableHotspotBuilder().build();
    }

    private static class VariantHotspotImpl implements VariantHotspot {

        @NotNull
        private final String gene;
        @NotNull
        private final String chromosome;
        private final int position;
        @NotNull
        private final String ref;
        @NotNull
        private final String alt;

        public VariantHotspotImpl(@NotNull final String gene, @NotNull final String chromosome, final int position,
                @NotNull final String ref, @NotNull final String alt) {
            this.gene = gene;
            this.chromosome = chromosome;
            this.position = position;
            this.ref = ref;
            this.alt = alt;
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
        public int position() {
            return position;
        }

        @NotNull
        @Override
        public String ref() {
            return ref;
        }

        @NotNull
        @Override
        public String alt() {
            return alt;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final VariantHotspotImpl that = (VariantHotspotImpl) o;
            return position == that.position && gene.equals(that.gene) && chromosome.equals(that.chromosome) && ref.equals(that.ref)
                    && alt.equals(that.alt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gene, chromosome, position, ref, alt);
        }

        @Override
        public String toString() {
            return "VariantHotspotImpl{" + "gene='" + gene + '\'' + ", chromosome='" + chromosome + '\'' + ", position=" + position
                    + ", ref='" + ref + '\'' + ", alt='" + alt + '\'' + '}';
        }
    }
}
