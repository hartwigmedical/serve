package com.hartwig.serve.sources.hartwig.hotspot;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.molecular.common.GeneRole;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.util.KeyFormatter;
import com.hartwig.serve.extraction.variant.ImmutableVariant;
import com.hartwig.serve.extraction.variant.KnownHotspotConsolidation;
import com.hartwig.serve.extraction.variant.ProteinResolver;
import com.hartwig.serve.extraction.variant.Variant;
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class HartwigHotspotExtractor {

    private static final Logger LOGGER = LogManager.getLogger(HartwigHotspotExtractor.class);

    @NotNull
    private final Knowledgebase source;
    @NotNull
    private final ProteinResolver proteinResolver;
    private final boolean addExplicitHotspots;

    public HartwigHotspotExtractor(@NotNull final Knowledgebase source, @NotNull final ProteinResolver proteinResolver,
            final boolean addExplicitHotspots) {
        this.source = source;
        this.proteinResolver = proteinResolver;
        this.addExplicitHotspots = addExplicitHotspots;
    }

    @NotNull
    public ExtractionResult extract(@NotNull List<HartwigHotspotEntry> entries) {
        ProgressTracker tracker = new ProgressTracker("Hartwig", entries.size());
        Set<KnownHotspot> knownHotspots =
                entries.parallelStream().flatMap(entry -> resolveHotspotsForEntry(entry, tracker)).collect(Collectors.toSet());

        // Even for Hartwig sources the extractor may generate duplicate hotspots, so we need to consolidate them.
        KnownEvents knownEvents = ImmutableKnownEvents.builder().hotspots(KnownHotspotConsolidation.consolidate(knownHotspots)).build();

        return ImmutableExtractionResult.builder().refGenomeVersion(source.refGenomeVersion()).knownEvents(knownEvents).build();
    }

    @NotNull
    private Stream<KnownHotspot> resolveHotspotsForEntry(@NotNull HartwigHotspotEntry entry, @NotNull ProgressTracker tracker) {
        List<Variant> hotspots = entry.proteinAnnotation().isEmpty()
                ? Collections.emptyList()
                : proteinResolver.resolve(entry.gene(), entry.transcript(), entry.proteinAnnotation());

        Optional<Variant> explicitHotspotOption = explicitHotspotOption(entry, hotspots);
        tracker.update();

        return Stream.concat(explicitHotspotOption.stream(), hotspots.stream())
                .map(hotspot -> ImmutableKnownHotspot.builder()
                        .from(hotspot)
                        .ref(hotspot.ref())
                        .alt(hotspot.alt())
                        .gene(entry.gene())
                        .geneRole(GeneRole.UNKNOWN)
                        .proteinEffect(ProteinEffect.UNKNOWN)
                        .inputTranscript(entry.transcript())
                        .inputProteinAnnotation(entry.proteinAnnotation())
                        .addSources(source)
                        .build());
    }

    @NotNull
    private Optional<Variant> explicitHotspotOption(@NotNull HartwigHotspotEntry entry, @NotNull List<Variant> hotspots) {
        if (addExplicitHotspots) {
            Variant explicitVariant = toHotspot(entry);
            if (!hotspots.contains(explicitVariant)) {
                if (entry.proteinAnnotation().isEmpty()) {
                    LOGGER.debug(" Adding hotspot '{}' since protein annotation is not provided", explicitVariant);
                } else {
                    LOGGER.debug(" Adding hotspot '{}' since it was not generated by protein resolving based on '{}'", explicitVariant,
                            KeyFormatter.toProteinKey(entry.gene(), entry.transcript(), entry.proteinAnnotation()));
                }
                return Optional.of(explicitVariant);
            }
        }
        return Optional.empty();
    }

    @NotNull
    private static Variant toHotspot(@NotNull HartwigHotspotEntry entry) {
        return ImmutableVariant.builder()
                .chromosome(entry.chromosome())
                .position(entry.position())
                .ref(entry.ref())
                .alt(entry.alt())
                .build();
    }
}
