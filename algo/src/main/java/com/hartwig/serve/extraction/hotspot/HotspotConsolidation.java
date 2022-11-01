package com.hartwig.serve.extraction.hotspot;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.extraction.util.ProteinEffectConsolidation;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HotspotConsolidation {

    private HotspotConsolidation() {
    }

    @NotNull
    public static Set<KnownHotspot> consolidate(@NotNull Iterable<KnownHotspot> hotspots) {
        Map<KnownHotspot, ConsolidatedData> dataPerConsolidatedHotspot = Maps.newHashMap();
        for (KnownHotspot hotspot : hotspots) {
            KnownHotspot key = createKey(hotspot);

            ConsolidatedData data = ConsolidatedData.fromHotspot(hotspot);
            if (dataPerConsolidatedHotspot.containsKey(key)) {
                dataPerConsolidatedHotspot.put(key, merge(data, dataPerConsolidatedHotspot.get(key)));
            } else {
                dataPerConsolidatedHotspot.put(key, data);
            }
        }

        Set<KnownHotspot> consolidatedHotspots = Sets.newHashSet();
        for (Map.Entry<KnownHotspot, ConsolidatedData> entry : dataPerConsolidatedHotspot.entrySet()) {
            ConsolidatedData consolidatedData = entry.getValue();
            consolidatedHotspots.add(ImmutableKnownHotspot.builder()
                    .from(entry.getKey())
                    .sources(consolidatedData.sources())
                    .gene(consolidatedData.gene())
                    .inputTranscript(consolidatedData.inputTranscript())
                    .inputProteinAnnotation(consolidatedData.inputProteinAnnotation())
                    .proteinEffect(consolidatedData.proteinEffect())
                    .build());
        }
        return consolidatedHotspots;
    }

    @NotNull
    private static KnownHotspot createKey(@NotNull KnownHotspot hotspot) {
        return ImmutableKnownHotspot.builder()
                .from(hotspot)
                .sources(Sets.newHashSet())
                .gene(Strings.EMPTY)
                .inputTranscript(Strings.EMPTY)
                .inputProteinAnnotation(Strings.EMPTY)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .build();
    }

    @NotNull
    private static ConsolidatedData merge(@NotNull ConsolidatedData data1, @NotNull ConsolidatedData data2) {
        String bestTranscript;
        String bestProteinAnnotation;

        // If both annotations either have or have no transcript annotation it does not matter which one we pick, but we do enforce a
        // choice to make sure hotspot files stay identical for identical inputs.
        boolean favorAnnotation1;
        if (data1.inputProteinAnnotation().equals(data2.inputProteinAnnotation())) {
            if (data1.inputTranscript() != null && data2.inputTranscript() != null) {
                favorAnnotation1 = data1.inputTranscript().compareTo(data2.inputTranscript()) > 0;
            } else {
                favorAnnotation1 = data1.inputTranscript() != null;
            }
        } else {
            favorAnnotation1 = data1.inputProteinAnnotation().compareTo(data2.inputProteinAnnotation()) > 0;
        }

        if (data1.inputTranscript() == null && data2.inputTranscript() == null) {
            bestTranscript = null;
            bestProteinAnnotation = favorAnnotation1 ? data1.inputProteinAnnotation() : data2.inputProteinAnnotation();
        } else if (data1.inputTranscript() == null) {
            bestTranscript = data2.inputTranscript();
            bestProteinAnnotation = data2.inputProteinAnnotation();
        } else if (data2.inputTranscript() == null) {
            bestTranscript = data1.inputTranscript();
            bestProteinAnnotation = data1.inputProteinAnnotation();
        } else {
            bestTranscript = favorAnnotation1 ? data1.inputTranscript() : data2.inputTranscript();
            bestProteinAnnotation = favorAnnotation1 ? data1.inputProteinAnnotation() : data2.inputProteinAnnotation();
        }

        Set<Knowledgebase> mergedSources = Sets.newHashSet();
        mergedSources.addAll(data1.sources());
        mergedSources.addAll(data2.sources());

        ProteinEffect mergedEffect = ProteinEffectConsolidation.merge(data1.proteinEffect(), data2.proteinEffect());
        return new ConsolidatedData(mergedSources, data1.gene(), bestTranscript, bestProteinAnnotation, mergedEffect);
    }

    private static class ConsolidatedData {

        @NotNull
        private final Set<Knowledgebase> sources;
        @NotNull
        private final String gene;
        @Nullable
        private final String inputTranscript;
        @NotNull
        private final String inputProteinAnnotation;
        @NotNull
        private final ProteinEffect proteinEffect;

        @NotNull
        public static ConsolidatedData fromHotspot(@NotNull KnownHotspot hotspot) {
            return new ConsolidatedData(hotspot.sources(),
                    hotspot.gene(),
                    hotspot.inputTranscript(),
                    hotspot.inputProteinAnnotation(),
                    hotspot.proteinEffect());
        }

        public ConsolidatedData(@NotNull final Set<Knowledgebase> sources, @NotNull final String gene,
                @Nullable final String inputTranscript, @NotNull final String inputProteinAnnotation,
                @NotNull final ProteinEffect proteinEffect) {
            this.sources = sources;
            this.gene = gene;
            this.inputTranscript = inputTranscript;
            this.inputProteinAnnotation = inputProteinAnnotation;
            this.proteinEffect = proteinEffect;
        }

        @NotNull
        public Set<Knowledgebase> sources() {
            return sources;
        }

        @NotNull
        public String gene() {
            return gene;
        }

        @Nullable
        public String inputTranscript() {
            return inputTranscript;
        }

        @NotNull
        public String inputProteinAnnotation() {
            return inputProteinAnnotation;
        }

        @NotNull
        public ProteinEffect proteinEffect() {
            return proteinEffect;
        }
    }
}
