package com.hartwig.serve.datamodel.common;

import java.util.Objects;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class CommonTestFactory {

    private CommonTestFactory() {
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
