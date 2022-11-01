package com.hartwig.serve.extraction.copynumber;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.extraction.util.ProteinEffectConsolidation;

import org.jetbrains.annotations.NotNull;

public final class CopyNumberConsolidation {

    private CopyNumberConsolidation() {
    }

    @NotNull
    public static Set<KnownCopyNumber> consolidate(@NotNull Iterable<KnownCopyNumber> copyNumbers) {
        Map<KnownCopyNumber, ConsolidatedData> dataPerConsolidatedCopyNumber = Maps.newHashMap();
        for (KnownCopyNumber copyNumber : copyNumbers) {
            KnownCopyNumber key = createKey(copyNumber);

            ConsolidatedData data = ConsolidatedData.fromCopyNumber(copyNumber);
            if (dataPerConsolidatedCopyNumber.containsKey(key)) {
                dataPerConsolidatedCopyNumber.put(key, merge(data, dataPerConsolidatedCopyNumber.get(key)));
            } else {
                dataPerConsolidatedCopyNumber.put(key, data);
            }
        }

        Set<KnownCopyNumber> consolidated = Sets.newHashSet();
        for (Map.Entry<KnownCopyNumber, ConsolidatedData> entry : dataPerConsolidatedCopyNumber.entrySet()) {
            ConsolidatedData consolidatedData = entry.getValue();
            consolidated.add(ImmutableKnownCopyNumber.builder()
                    .from(entry.getKey())
                    .sources(consolidatedData.sources())
                    .proteinEffect(consolidatedData.proteinEffect())
                    .build());
        }
        return consolidated;
    }

    @NotNull
    private static KnownCopyNumber createKey(@NotNull KnownCopyNumber copyNumber) {
        return ImmutableKnownCopyNumber.builder().from(copyNumber).sources(Sets.newHashSet()).proteinEffect(ProteinEffect.UNKNOWN).build();
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
        public static ConsolidatedData fromCopyNumber(@NotNull KnownCopyNumber copyNumber) {
            return new ConsolidatedData(copyNumber.sources(), copyNumber.proteinEffect());
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
