package com.hartwig.serve.extraction;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ClinicalTrial;
import com.hartwig.serve.datamodel.EfficacyEvidence;
import com.hartwig.serve.datamodel.ImmutableEfficacyEvidence;
import com.hartwig.serve.datamodel.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.KnownEvents;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.codon.CodonConsolidation;
import com.hartwig.serve.extraction.copynumber.CopyNumberConsolidation;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.exon.ExonConsolidation;
import com.hartwig.serve.extraction.fusion.FusionConsolidation;
import com.hartwig.serve.extraction.gene.GeneConsolidation;
import com.hartwig.serve.extraction.hotspot.HotspotConsolidation;

import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ExtractionFunctions {

    private static final Logger LOGGER = LogManager.getLogger(ExtractionFunctions.class);

    private ExtractionFunctions() {
    }

    @NotNull
    public static ExtractionResult merge(@NotNull List<ExtractionResult> results) {
        RefGenome version = uniqueVersion(results);
        Set<EventInterpretation> mergedInterpretations = Sets.newHashSet();
        ImmutableKnownEvents.Builder unconsolidatedKnownEventsBuilder = ImmutableKnownEvents.builder();
        List<EfficacyEvidence> unconsolidatedEvidences = Lists.newArrayList();
        List<ClinicalTrial> mergedTrials = Lists.newArrayList();

        for (ExtractionResult result : results) {
            mergedInterpretations.addAll(result.eventInterpretations());
            if (result.knownEvents() != null) {
                unconsolidatedKnownEventsBuilder.from(result.knownEvents());
            }
            if (result.efficacyEvidences() != null) {
                unconsolidatedEvidences.addAll(result.efficacyEvidences());
            }
            if (result.clinicalTrials() != null) {
                mergedTrials.addAll(result.clinicalTrials());
            }
        }

        // TODO (KD): We used to consolidate URLs for evidence in the past but not sure that is still necessary.
        return ImmutableExtractionResult.builder()
                .refGenomeVersion(version)
                .eventInterpretations(mergedInterpretations)
                .knownEvents(consolidateKnownEvents(unconsolidatedKnownEventsBuilder.build()))
                .efficacyEvidences(consolidateEvidences(unconsolidatedEvidences))
                .clinicalTrials(mergedTrials)
                .build();
    }

    @NotNull
    private static RefGenome uniqueVersion(@NotNull List<ExtractionResult> results) {
        if (results.isEmpty()) {
            RefGenome defaultVersion = RefGenome.V38;
            LOGGER.warn("Cannot extract ref genome version for empty list of results. Reverting to default {}", defaultVersion);
            return defaultVersion;
        }

        RefGenome version = results.get(0).refGenomeVersion();
        for (ExtractionResult result : results) {
            if (result.refGenomeVersion() != version) {
                throw new IllegalStateException("Ref genome version is not unique amongst list of extraction results");
            }
        }

        return version;
    }

    @NotNull
    private static KnownEvents consolidateKnownEvents(@NotNull KnownEvents unconsolidated) {
        return ImmutableKnownEvents.builder()
                .hotspots(HotspotConsolidation.consolidate(unconsolidated.hotspots()))
                .codons(CodonConsolidation.consolidate(unconsolidated.codons()))
                .exons(ExonConsolidation.consolidate(unconsolidated.exons()))
                .genes(GeneConsolidation.consolidate(unconsolidated.genes()))
                .copyNumbers(CopyNumberConsolidation.consolidate(unconsolidated.copyNumbers()))
                .fusions(FusionConsolidation.consolidate(unconsolidated.fusions()))
                .build();
    }

    @NotNull
    private static List<EfficacyEvidence> consolidateEvidences(@NotNull List<EfficacyEvidence> unconsolidatedEvidences) {
        Map<EfficacyEvidence, Set<String>> urlsPerEvidence = Maps.newHashMap();
        for (EfficacyEvidence evidence : unconsolidatedEvidences) {
            EfficacyEvidence stripped = ImmutableEfficacyEvidence.builder().from(evidence).urls(Set.of()).build();
            Set<String> urls = urlsPerEvidence.get(stripped);
            if (urls == null) {
                urls = Sets.newTreeSet();
            }
            urls.addAll(evidence.urls());
            urlsPerEvidence.put(stripped, urls);
        }

        List<EfficacyEvidence> consolidatedEvents = Lists.newArrayList();
        for (Map.Entry<EfficacyEvidence, Set<String>> entry : urlsPerEvidence.entrySet()) {
            consolidatedEvents.add(ImmutableEfficacyEvidence.builder().from(entry.getKey()).urls(entry.getValue()).build());
        }
        return consolidatedEvents;
    }
}

