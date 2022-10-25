package com.hartwig.serve.extraction.copynumber;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.gene.CopyNumberType;
import com.hartwig.serve.datamodel.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;

import org.jetbrains.annotations.NotNull;

public final class CopyNumberFunctions {

    private CopyNumberFunctions() {
    }

    @NotNull
    public static Set<KnownCopyNumber> consolidate(@NotNull Iterable<KnownCopyNumber> copyNumbers) {
        Map<CopyNumberKey, Set<Knowledgebase>> sourcesPerCopyNumber = Maps.newHashMap();
        for (KnownCopyNumber copyNumber : copyNumbers) {
            CopyNumberKey key = new CopyNumberKey(copyNumber.gene(), copyNumber.geneRole(), copyNumber.proteinEffect(), copyNumber.type());
            Set<Knowledgebase> sources = sourcesPerCopyNumber.get(key);
            if (sources == null) {
                sources = Sets.newHashSet();
            }
            sources.addAll(copyNumber.sources());
            sourcesPerCopyNumber.put(key, sources);
        }

        Set<KnownCopyNumber> consolidated = Sets.newHashSet();
        for (Map.Entry<CopyNumberKey, Set<Knowledgebase>> entry : sourcesPerCopyNumber.entrySet()) {
            consolidated.add(ImmutableKnownCopyNumber.builder()
                    .gene(entry.getKey().gene())
                    .geneRole(entry.getKey().geneRole())
                    .proteinEffect(entry.getKey().proteinEffect())
                    .type(entry.getKey().type())
                    .sources(entry.getValue())
                    .build());
        }
        return consolidated;
    }

    private static class CopyNumberKey {

        @NotNull
        private final String gene;
        @NotNull
        private final GeneRole geneRole;
        @NotNull
        private final ProteinEffect proteinEffect;
        @NotNull
        private final CopyNumberType type;

        public CopyNumberKey(@NotNull final String gene, @NotNull final GeneRole geneRole, @NotNull final ProteinEffect proteinEffect,
                @NotNull final CopyNumberType type) {
            this.gene = gene;
            this.geneRole = geneRole;
            this.proteinEffect = proteinEffect;
            this.type = type;
        }

        @NotNull
        public String gene() {
            return gene;
        }

        @NotNull
        public GeneRole geneRole() {
            return geneRole;
        }

        @NotNull
        public ProteinEffect proteinEffect() {
            return proteinEffect;
        }

        @NotNull
        public CopyNumberType type() {
            return type;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final CopyNumberKey that = (CopyNumberKey) o;
            return gene.equals(that.gene) && geneRole == that.geneRole && proteinEffect == that.proteinEffect && type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(gene, geneRole, proteinEffect, type);
        }
    }
}
