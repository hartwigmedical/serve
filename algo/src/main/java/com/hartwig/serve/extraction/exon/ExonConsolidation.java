package com.hartwig.serve.extraction.exon;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.molecular.range.KnownExon;
import com.hartwig.serve.extraction.util.ProteinEffectConsolidation;

import org.jetbrains.annotations.NotNull;

public final class ExonConsolidation {

    private ExonConsolidation() {
    }

    @NotNull
    public static Set<KnownExon> consolidate(@NotNull Iterable<KnownExon> exons) {
        Map<KnownExon, ConsolidatedData> dataPerConsolidatedExon = Maps.newHashMap();
        for (KnownExon exon : exons) {
            KnownExon key = createKey(exon);

            ConsolidatedData data = ConsolidatedData.fromExon(exon);
            if (dataPerConsolidatedExon.containsKey(key)) {
                dataPerConsolidatedExon.put(key, merge(data, dataPerConsolidatedExon.get(key)));
            } else {
                dataPerConsolidatedExon.put(key, data);
            }
        }

        Set<KnownExon> consolidated = Sets.newHashSet();
        for (Map.Entry<KnownExon, ConsolidatedData> entry : dataPerConsolidatedExon.entrySet()) {
            ConsolidatedData consolidatedData = entry.getValue();
            consolidated.add(ImmutableKnownExon.builder()
                    .from(entry.getKey())
                    .sources(consolidatedData.sources())
                    .proteinEffect(consolidatedData.proteinEffect())
                    .build());
        }
        return consolidated;
    }

    @NotNull
    private static KnownExon createKey(@NotNull KnownExon exon) {
        return ImmutableKnownExon.builder().from(exon).sources(Sets.newHashSet()).proteinEffect(ProteinEffect.UNKNOWN).build();
    }

    @NotNull
    private static ConsolidatedData merge(@NotNull ConsolidatedData data1, @NotNull ConsolidatedData data2) {
        Set<Knowledgebase> sources = Sets.newHashSet();
        sources.addAll(data1.sources());
        sources.addAll(data2.sources());

        return new ConsolidatedData(sources, ProteinEffectConsolidation.merge(data1.proteinEffect(), data2.proteinEffect()));
    }

    private static class ConsolidatedData {

        @NotNull
        private final Set<Knowledgebase> sources;
        @NotNull
        private final ProteinEffect proteinEffect;

        @NotNull
        public static ConsolidatedData fromExon(@NotNull KnownExon exon) {
            return new ConsolidatedData(exon.sources(), exon.proteinEffect());
        }

        ConsolidatedData(@NotNull final Set<Knowledgebase> sources, @NotNull final ProteinEffect proteinEffect) {
            this.sources = sources;
            this.proteinEffect = proteinEffect;
        }

        @NotNull
        public Set<Knowledgebase> sources() {
            return sources;
        }

        @NotNull
        public ProteinEffect proteinEffect() {
            return proteinEffect;
        }
    }
}
