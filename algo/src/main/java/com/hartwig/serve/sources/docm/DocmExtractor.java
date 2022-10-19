package com.hartwig.serve.sources.docm;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.common.variant.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.hotspot.HotspotFunctions;
import com.hartwig.serve.extraction.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.extraction.hotspot.KnownHotspot;
import com.hartwig.serve.extraction.hotspot.ProteinResolver;
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
        Set<KnownHotspot> knownHotspots = Sets.newHashSet();
        ProgressTracker tracker = new ProgressTracker("DoCM", entries.size());
        for (DocmEntry entry : entries) {
            List<VariantHotspot> hotspots = proteinResolver.resolve(entry.gene(), entry.transcript(), entry.proteinAnnotation());

            for (VariantHotspot hotspot : hotspots) {
                knownHotspots.add(ImmutableKnownHotspot.builder()
                        .from(hotspot)
                        .addSources(Knowledgebase.DOCM)
                        .gene(entry.gene())
                        .transcript(entry.transcript())
                        .proteinAnnotation(entry.proteinAnnotation())
                        .build());
            }

            tracker.update();
        }

        // Hotspots appear multiple times in DoCM on different transcripts. We need to consolidate even though there is only one source.
        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.DOCM.refGenomeVersion())
                .knownHotspots(HotspotFunctions.consolidate(knownHotspots))
                .build();
    }
}
