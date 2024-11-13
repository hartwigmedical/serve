package com.hartwig.serve.extraction.fusion;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.molecular.fusion.KnownFusion;
import com.hartwig.serve.extraction.util.ProteinEffectConsolidation;

import org.jetbrains.annotations.NotNull;

public final class FusionConsolidation {

    private FusionConsolidation() {
    }

    @NotNull
    public static Set<KnownFusion> consolidate(@NotNull Iterable<KnownFusion> fusions) {
        Map<KnownFusion, ConsolidatedData> dataPerConsolidatedFusion = Maps.newHashMap();
        for (KnownFusion fusion : fusions) {
            KnownFusion key = createKey(fusion);

            ConsolidatedData data = ConsolidatedData.fromFusion(fusion);
            if (dataPerConsolidatedFusion.containsKey(key)) {
                dataPerConsolidatedFusion.put(key, merge(data, dataPerConsolidatedFusion.get(key)));
            } else {
                dataPerConsolidatedFusion.put(key, data);
            }
        }

        Set<KnownFusion> consolidated = Sets.newHashSet();
        for (Map.Entry<KnownFusion, ConsolidatedData> entry : dataPerConsolidatedFusion.entrySet()) {
            ConsolidatedData consolidatedData = entry.getValue();
            consolidated.add(ImmutableKnownFusion.builder()
                    .from(entry.getKey())
                    .sources(consolidatedData.sources())
                    .proteinEffect(consolidatedData.proteinEffect())
                    .build());
        }
        return consolidated;
    }

    @NotNull
    private static KnownFusion createKey(@NotNull KnownFusion fusion) {
        return ImmutableKnownFusion.builder().from(fusion).sources(Sets.newHashSet()).proteinEffect(ProteinEffect.UNKNOWN).build();
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
        public static ConsolidatedData fromFusion(@NotNull KnownFusion fusion) {
            return new ConsolidatedData(fusion.sources(), fusion.proteinEffect());
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
