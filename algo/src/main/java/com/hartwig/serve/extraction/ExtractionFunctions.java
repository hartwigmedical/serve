package com.hartwig.serve.extraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.efficacy.ImmutableEfficacyEvidence;
import com.hartwig.serve.datamodel.molecular.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.datamodel.trial.ImmutableActionableTrial;
import com.hartwig.serve.extraction.codon.CodonConsolidation;
import com.hartwig.serve.extraction.copynumber.CopyNumberConsolidation;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.exon.ExonConsolidation;
import com.hartwig.serve.extraction.fusion.FusionConsolidation;
import com.hartwig.serve.extraction.gene.GeneConsolidation;
import com.hartwig.serve.extraction.variant.KnownHotspotConsolidation;

import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ExtractionFunctions {

    private static final Logger LOGGER = LogManager.getLogger(ExtractionFunctions.class);

    private ExtractionFunctions() {
    }

    @NotNull
    public static ExtractionResult merge(@NotNull List<ExtractionResult> results) {
        RefGenome version = uniqueVersion(results);
        Set<EventInterpretation> mergedInterpretations = Sets.newHashSet();
        ImmutableKnownEvents.Builder unconsolidatedKnownEventsBuilder = null;
        List<EfficacyEvidence> unconsolidatedEvidences = null;
        List<ActionableTrial> unconsolidatedTrials = null;

        for (ExtractionResult result : results) {
            mergedInterpretations.addAll(result.eventInterpretations());
            if (result.knownEvents() != null) {
                if (unconsolidatedKnownEventsBuilder == null) {
                    unconsolidatedKnownEventsBuilder = ImmutableKnownEvents.builder();
                }
                unconsolidatedKnownEventsBuilder.from(result.knownEvents());
            }
            if (result.evidences() != null) {
                if (unconsolidatedEvidences == null) {
                    unconsolidatedEvidences = Lists.newArrayList();
                }
                unconsolidatedEvidences.addAll(result.evidences());
            }
            if (result.trials() != null) {
                if (unconsolidatedTrials == null) {
                    unconsolidatedTrials = Lists.newArrayList();
                }
                unconsolidatedTrials.addAll(result.trials());
            }
        }

        KnownEvents unconsolidatedKnownEvents = unconsolidatedKnownEventsBuilder != null ? unconsolidatedKnownEventsBuilder.build() : null;

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(version)
                .eventInterpretations(mergedInterpretations)
                .knownEvents(consolidateKnownEvents(unconsolidatedKnownEvents))
                .evidences(consolidateEvidences(unconsolidatedEvidences))
                .trials(consolidateTrials(unconsolidatedTrials))
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

    @Nullable
    private static KnownEvents consolidateKnownEvents(@Nullable KnownEvents unconsolidated) {
        if (unconsolidated == null) {
            return null;
        }

        return ImmutableKnownEvents.builder()
                .hotspots(KnownHotspotConsolidation.consolidate(unconsolidated.hotspots()))
                .codons(CodonConsolidation.consolidate(unconsolidated.codons()))
                .exons(ExonConsolidation.consolidate(unconsolidated.exons()))
                .genes(GeneConsolidation.consolidate(unconsolidated.genes()))
                .copyNumbers(CopyNumberConsolidation.consolidate(unconsolidated.copyNumbers()))
                .fusions(FusionConsolidation.consolidate(unconsolidated.fusions()))
                .build();
    }

    @Nullable
    private static List<EfficacyEvidence> consolidateEvidences(@Nullable List<EfficacyEvidence> unconsolidatedEvidences) {
        if (unconsolidatedEvidences == null) {
            return null;
        }

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

    @Nullable
    private static List<ActionableTrial> consolidateTrials(@Nullable List<ActionableTrial> unconsolidatedTrials) {
        if (unconsolidatedTrials == null) {
            return null;
        }

        List<ActionableTrial> consolidatedTrials = new ArrayList<>();
        Set<Knowledgebase> sources = unconsolidatedTrials.stream().map(ActionableTrial::source).collect(Collectors.toSet());
        for (Knowledgebase source : sources) {
            Set<String> uniqueNctIds = unconsolidatedTrials.stream()
                    .filter(trial -> trial.source() == source)
                    .map(ActionableTrial::nctId)
                    .collect(Collectors.toSet());

            for (String nctId : uniqueNctIds) {
                List<ActionableTrial> trialsForSingleNctId = unconsolidatedTrials.stream()
                        .filter(trial -> trial.source() == source && trial.nctId().equals(nctId))
                        .collect(Collectors.toList());

                consolidatedTrials.add(consolidateTrialsForNctId(trialsForSingleNctId));
            }
        }

        return consolidatedTrials;
    }

    @NotNull
    private static ActionableTrial consolidateTrialsForNctId(@NotNull List<ActionableTrial> trialsForSingleNctId) {
        return ImmutableActionableTrial.builder()
                .source(unique(trialsForSingleNctId.stream().map(ActionableTrial::source)))
                .nctId(unique(trialsForSingleNctId.stream().map(ActionableTrial::nctId)))
                .title(unique(trialsForSingleNctId.stream().map(ActionableTrial::title)))
                .acronym(unique(trialsForSingleNctId.stream().map(ActionableTrial::acronym)))
                .countries(unique(trialsForSingleNctId.stream().map(ActionableTrial::countries)))
                .therapyNames(unique(trialsForSingleNctId.stream().map(ActionableTrial::therapyNames)))
                .genderCriterium(unique(trialsForSingleNctId.stream().map(ActionableTrial::genderCriterium)))
                .indications(mergeSet(trialsForSingleNctId.stream().map(ActionableTrial::indications)))
                .anyMolecularCriteria(mergeSet(trialsForSingleNctId.stream().map(ActionableTrial::anyMolecularCriteria)))
                .urls(unique(trialsForSingleNctId.stream().map(ActionableTrial::urls)))
                .build();
    }

    @NotNull
    private static <T> Set<T> mergeSet(@NotNull Stream<Set<T>> streamOfSets) {
        return streamOfSets.flatMap(Set::stream).collect(Collectors.toSet());
    }

    @Nullable
    private static <T> T unique(@NotNull Stream<T> stream) {
        Set<T> set = stream.collect(Collectors.toSet());
        if (set.size() != 1) {
            throw new IllegalStateException("Set does not contain exactly 1 element: " + set);
        }

        return set.iterator().next();
    }
}

