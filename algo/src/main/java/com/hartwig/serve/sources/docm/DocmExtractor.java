package com.hartwig.serve.sources.docm;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.molecular.common.GeneRole;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.variant.KnownHotspotConsolidation;
import com.hartwig.serve.extraction.variant.ProteinResolver;
import com.hartwig.serve.extraction.variant.Variant;
import com.hartwig.serve.util.ProgressTracker;

import org.jetbrains.annotations.NotNull;

public class DocmExtractor {

    @NotNull
    private final ProteinResolver proteinResolver;

    public DocmExtractor(@NotNull final ProteinResolver proteinResolver) {
        this.proteinResolver = proteinResolver;
    }

    @NotNull
    public ExtractionResult extract(@NotNull List<DocmEntry> entries) {
        ProgressTracker tracker = new ProgressTracker("DoCM", entries.size());
        Set<KnownHotspot> knownHotspots = entries.parallelStream().flatMap(entry -> {
            List<Variant> hotspots = proteinResolver.resolve(entry.gene(), entry.transcript(), entry.proteinAnnotation());
            tracker.update();
            return hotspots.stream()
                    .map(hotspot -> ImmutableKnownHotspot.builder()
                            .from(hotspot)
                            .ref(hotspot.ref())
                            .alt(hotspot.alt())
                            .gene(entry.gene())
                            .geneRole(GeneRole.UNKNOWN)
                            .proteinEffect(ProteinEffect.UNKNOWN)
                            .inputTranscript(entry.transcript())
                            .inputProteinAnnotation(entry.proteinAnnotation())
                            .addSources(Knowledgebase.DOCM)
                            .build());
        }).collect(Collectors.toSet());

        // Hotspots appear multiple times in DoCM on different transcripts. We need to consolidate even though there is only one source.
        KnownEvents knownEvents = ImmutableKnownEvents.builder().hotspots(KnownHotspotConsolidation.consolidate(knownHotspots)).build();

        return ImmutableExtractionResult.builder().refGenomeVersion(Knowledgebase.DOCM.refGenomeVersion()).knownEvents(knownEvents).build();
    }
}
