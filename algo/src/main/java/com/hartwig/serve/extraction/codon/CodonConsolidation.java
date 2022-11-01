package com.hartwig.serve.extraction.codon;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.extraction.util.ProteinEffectConsolidation;

import org.jetbrains.annotations.NotNull;

public final class CodonConsolidation {

    private CodonConsolidation() {
    }

    @NotNull
    public static Set<KnownCodon> consolidate(@NotNull Iterable<KnownCodon> codons) {
        Map<KnownCodon, ConsolidatedData> dataPerConsolidatedCodon = Maps.newHashMap();
        for (KnownCodon codon : codons) {
            KnownCodon key = createKey(codon);

            ConsolidatedData data = ConsolidatedData.fromCodon(codon);
            if (dataPerConsolidatedCodon.containsKey(key)) {
                dataPerConsolidatedCodon.put(key, merge(data, dataPerConsolidatedCodon.get(key)));
            } else {
                dataPerConsolidatedCodon.put(key, data);
            }
        }

        Set<KnownCodon> consolidated = Sets.newHashSet();
        for (Map.Entry<KnownCodon, ConsolidatedData> entry : dataPerConsolidatedCodon.entrySet()) {
            ConsolidatedData consolidatedData = entry.getValue();
            consolidated.add(ImmutableKnownCodon.builder()
                    .from(entry.getKey())
                    .sources(consolidatedData.sources())
                    .proteinEffect(consolidatedData.proteinEffect())
                    .build());
        }
        return consolidated;
    }

    @NotNull
    private static KnownCodon createKey(@NotNull KnownCodon codon) {
        return ImmutableKnownCodon.builder().from(codon).sources(Sets.newHashSet()).proteinEffect(ProteinEffect.UNKNOWN).build();
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
        public static ConsolidatedData fromCodon(@NotNull KnownCodon codon) {
            return new ConsolidatedData(codon.sources(), codon.proteinEffect());
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
