package com.hartwig.serve.extraction.copynumber;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.gene.GeneEvent;
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
            CopyNumberKey key = new CopyNumberKey(copyNumber.gene(), copyNumber.geneRole(), copyNumber.proteinEffect(), copyNumber.event());
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
                    .event(entry.getKey().event())
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
        private final GeneEvent event;

        public CopyNumberKey(@NotNull final String gene, @NotNull final GeneRole geneRole, @NotNull final ProteinEffect proteinEffect,
                @NotNull final GeneEvent event) {
            this.gene = gene;
            this.geneRole = geneRole;
            this.proteinEffect = proteinEffect;
            this.event = event;
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
            final CopyNumberKey that = (CopyNumberKey) o;
            return gene.equals(that.gene) && geneRole == that.geneRole && proteinEffect == that.proteinEffect && event == that.event;
        }

        @Override
        public int hashCode() {
            return Objects.hash(gene, geneRole, proteinEffect, event);
        }
    }
}
