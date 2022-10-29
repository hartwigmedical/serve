package com.hartwig.serve.datamodel.hotspot;

import java.util.Objects;

import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.CommonTestFactory;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HotspotTestFactory {

    private HotspotTestFactory() {
    }

    @NotNull
    public static VariantHotspot createVariantHotspot(@NotNull String gene, @NotNull GeneRole geneRole,
            @NotNull ProteinEffect proteinEffect, @Nullable Boolean associatedWithDrugResistance, @NotNull String chromosome, int position,
            @NotNull String ref, @NotNull String alt) {
        return new VariantHotspotImpl(gene, geneRole, proteinEffect, associatedWithDrugResistance, chromosome, position, ref, alt);
    }

    @NotNull
    public static ImmutableKnownHotspot.Builder knownHotspotBuilder() {
        return ImmutableKnownHotspot.builder()
                .from(CommonTestFactory.createEmptyGeneAlteration())
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .transcript(null)
                .proteinAnnotation(Strings.EMPTY);
    }

    @NotNull
    public static KnownHotspot createTestKnownHotspotForSource(@NotNull Knowledgebase source) {
        return knownHotspotBuilder().addSources(source).build();
    }

    @NotNull
    public static KnownHotspot createTestKnownHotspot() {
        return knownHotspotBuilder().build();
    }

    @NotNull
    public static ImmutableActionableHotspot.Builder actionableHotspotBuilder() {
        return ImmutableActionableHotspot.builder()
                .from(DatamodelTestFactory.createEmptyActionableEvent())
                .from(CommonTestFactory.createEmptyGeneAlteration())
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY);
    }

    @NotNull
    public static ActionableHotspot createTestActionableHotspotForSource(@NotNull Knowledgebase source) {
        return actionableHotspotBuilder().source(source).build();
    }

    private static class VariantHotspotImpl implements VariantHotspot {

        @NotNull
        private final String gene;
        @NotNull
        private final GeneRole geneRole;
        @NotNull
        private final ProteinEffect proteinEffect;
        @Nullable
        private final Boolean associatedWithDrugResistance;
        @NotNull
        private final String chromosome;
        private final int position;
        @NotNull
        private final String ref;
        @NotNull
        private final String alt;

        public VariantHotspotImpl(@NotNull final String gene, @NotNull final GeneRole geneRole, @NotNull final ProteinEffect proteinEffect,
                @Nullable final Boolean associatedWithDrugResistance, @NotNull final String chromosome, final int position,
                @NotNull final String ref, @NotNull final String alt) {
            this.gene = gene;
            this.geneRole = geneRole;
            this.proteinEffect = proteinEffect;
            this.associatedWithDrugResistance = associatedWithDrugResistance;
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
            return position == that.position && gene.equals(that.gene) && geneRole == that.geneRole && proteinEffect == that.proteinEffect
                    && Objects.equals(associatedWithDrugResistance, that.associatedWithDrugResistance) && chromosome.equals(that.chromosome)
                    && ref.equals(that.ref) && alt.equals(that.alt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gene, geneRole, proteinEffect, associatedWithDrugResistance, chromosome, position, ref, alt);
        }

        @Override
        public String toString() {
            return "VariantHotspotImpl{" + "gene='" + gene + '\'' + ", geneRole=" + geneRole + ", proteinEffect=" + proteinEffect
                    + ", associatedWithDrugResistance=" + associatedWithDrugResistance + ", chromosome='" + chromosome + '\''
                    + ", position=" + position + ", ref='" + ref + '\'' + ", alt='" + alt + '\'' + '}';
        }
    }
}
