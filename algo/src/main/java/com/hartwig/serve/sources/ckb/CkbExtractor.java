package com.hartwig.serve.sources.ckb;

import static com.hartwig.serve.sources.ckb.CkbMolecularCriteriaExtractor.combinedSourceEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
import com.hartwig.serve.ckb.classification.CkbEventTypeExtractor;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.molecular.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ExtractionFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.events.ImmutableEventInterpretation;
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbExtractor {

    private static final Logger LOGGER = LogManager.getLogger(CkbExtractor.class);
    private static final String VARIANT_DELIMITER = ",";
    private static final String GENE_DELIMITER = ",";
    private static final String EVENT_DELIMITER = ",";

    // these constants should be in ActionableTrialFactory I think
    private static final String VARIANT_REQUIRED = "required";
    private static final String VARIANT_PARTIAL_REQUIRED = "partial - required";

    @NotNull
    private final EventExtractor eventExtractor;
    @NotNull
    private final EfficacyEvidenceFactory efficacyEvidenceFactory;
    @NotNull
    private final ActionableTrialFactory actionableTrialFactory;

    CkbExtractor(@NotNull final EventExtractor eventExtractor, @NotNull EfficacyEvidenceFactory efficacyEvidenceFactory,
            @NotNull ActionableTrialFactory actionableTrialFactory) {
        this.eventExtractor = eventExtractor;
        this.efficacyEvidenceFactory = efficacyEvidenceFactory;
        this.actionableTrialFactory = actionableTrialFactory;
    }

    @NotNull
    public ExtractionResult extract(@NotNull List<CkbEntry> entries) {

        LOGGER.info("total number of ckb entries: {}", entries.size());

        // TODO we are handling trials 'out-of-band' here, can rework into the regular flow
        //  with some additional complexity
        List<ActionableTrial> trials = processTrials(entries);
        LOGGER.info("total number of alternative trials: {}", trials.size());

        ProgressTracker tracker = new ProgressTracker("CKB", entries.size());
        // Assume entries without variants are filtered out prior to extraction
        List<ExtractionResult> extractions = entries.parallelStream()
                .map(this::getExtractionResult)
                .peek(e -> tracker.update())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ImmutableExtractionResult.builder()
                .from(ExtractionFunctions.merge(extractions))
                .trials(trials)
                .build();
    }

    @Nullable
    private ExtractionResult getExtractionResult(@NotNull CkbEntry entry) {
        if (entry.variants().isEmpty()) {
            throw new IllegalStateException("A CKB entry without variants has been provided for extraction: " + entry);
        }

        List<String> genes = entry.variants().stream()
                .map(CkbEventAndGeneExtractor::extractGene)
                .collect(Collectors.toList());

        String sourceEvent = combinedSourceEvent(entry);

        if (entry.type() == EventType.UNKNOWN) {
            LOGGER.warn("No event type known for CKB Profile '{}' for event '{}'", entry.profileId(), sourceEvent);
            return null;
        } else {

            EventInterpretation interpretation = ImmutableEventInterpretation.builder()
                    .source(Knowledgebase.CKB)
                    .sourceEvent(sourceEvent)
                    .interpretedGene(String.join(" ", genes))
                    .interpretedEvent(sourceEvent)
                    .interpretedEventType(entry.type())
                    .build();

            List<VariantWithExtraction> variantWithExtraction = extractEventCriteria(entry);
            Set<EventExtractorOutput> eventExtractionOutput =
                    variantWithExtraction.stream().map(g -> g.eventExtractorOutput).collect(Collectors.toSet());
            MolecularCriterium molecularCriterium = CkbMolecularCriteriaExtractor.createMolecularCriterium(entry, eventExtractionOutput);

            Set<EfficacyEvidence> efficacyEvidences =
                    efficacyEvidenceFactory.create(entry, Set.of(molecularCriterium), sourceEvent, String.join(GENE_DELIMITER, genes));

            List<KnownEvents> knownEvents = variantWithExtraction.stream()
                    .map(extraction -> CkbKnownEventsGenerator.generateKnownEvents(extraction.eventExtractorOutput,
                            efficacyEvidences.isEmpty(),
                            extraction.variant,
                            extraction.event,
                            extraction.gene))
                    .collect(Collectors.toList());
            KnownEvents combinedKnownEvents =
                    knownEvents.stream().reduce(CkbKnownEventsGenerator::mergeKnownEvents)
                            .orElse(ImmutableKnownEvents.builder().build());

            return ImmutableExtractionResult.builder()
                    .refGenomeVersion(Knowledgebase.CKB.refGenomeVersion())
                    .eventInterpretations(Set.of(interpretation))
                    .knownEvents(combinedKnownEvents)
                    .evidences(efficacyEvidences)
                    .trials(Set.of())
                    .build();
        }
    }

    @NotNull
    private List<VariantWithExtraction> extractEventCriteria(@NotNull CkbEntry entry) {
        return entry.variants().stream()
                .map(this::extractVariantCriteria)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // intermediate container, might be able to refactor away somehow
    private static class VariantWithExtraction {

        @NotNull
        private final String gene;
        @NotNull
        private final String event;
        @NotNull
        private final Variant variant;
        @NotNull
        private final EventExtractorOutput eventExtractorOutput;

        private VariantWithExtraction(@NotNull String gene, @NotNull String event, @NotNull Variant variant,
                @NotNull EventExtractorOutput eventExtractorOutput) {
            this.gene = gene;
            this.event = event;
            this.variant = variant;
            this.eventExtractorOutput = eventExtractorOutput;
        }
    }

    @Nullable
    private VariantWithExtraction extractVariantCriteria(@NotNull Variant variant) {
        EventType eventType = CkbEventTypeExtractor.classify(variant);

        if (eventType == EventType.COMBINED) {
            throw new IllegalStateException("Should not have combined event for single variant: " + variant.fullName());
        } else if (eventType == EventType.UNKNOWN) {
            LOGGER.warn("No known event type for variant: '{}'", variant.fullName());
            return null;
        }

        String event = CkbEventAndGeneExtractor.extractEvent(variant);
        String gene = CkbEventAndGeneExtractor.extractGene(variant);

        EventExtractorOutput eventExtractorOutput =
                CkbMolecularCriteriaExtractor.curateCodons(eventExtractor.extract(gene, null, eventType, event));
        return new VariantWithExtraction(gene, event, variant, eventExtractorOutput);
    }

    @NotNull
    private static String concat(@NotNull List<Variant> variants) {
        return variants.stream().map(Variant::variant).collect(Collectors.joining(VARIANT_DELIMITER));
    }

    @NotNull
    public List<ActionableTrial> processTrials(@NotNull List<CkbEntry> ckbEntries) {
        Map<Integer, CkbEntry> idToEntry = ckbEntries.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(CkbEntry::profileId, entry -> entry),
                        Collections::unmodifiableMap));

        Set<ClinicalTrial> uniqueTrials = ckbEntries.stream()
                .flatMap(entry -> entry.clinicalTrials().stream())
                .collect(Collectors.toSet());

        List<ActionableTrial> allActionableTrials = uniqueTrials.parallelStream()
                .map(trial -> processTrial(trial, idToEntry))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        logSummaryStats(allActionableTrials);
        return allActionableTrials;
    }

    @Nullable
    private ActionableTrial processTrial(@NotNull ClinicalTrial trial, @NotNull Map<Integer, CkbEntry> idToEntry) {
        LOGGER.debug("trial: {}", trial.nctId());

        ArrayList<MolecularCriterium> requiredCriterium = new ArrayList<>();
        ArrayList<MolecularCriterium> partiallyRequiredCriterium = new ArrayList<>();
        ArrayList<VariantWithExtraction> allVariantWithExtraction = new ArrayList<>();

        for (VariantRequirementDetail details : trial.variantRequirementDetails()) {
            Integer profileId = details.profileId();
            if (details.requirementType().equals(VARIANT_REQUIRED) || details.requirementType().equals(VARIANT_PARTIAL_REQUIRED)) {
                CkbEntry entry = idToEntry.get(profileId);

                if (entry == null) {
                    LOGGER.warn("Skipping profile {} for trial {} because profile was filtered", profileId, trial.nctId());
                } else {
                    List<VariantWithExtraction> variantWithExtractions = extractEventCriteria(entry);
                    allVariantWithExtraction.addAll(variantWithExtractions);
                    Set<EventExtractorOutput> eventExtractionOutput = variantWithExtractions.stream()
                            .map(extraction -> extraction.eventExtractorOutput)
                            .collect(Collectors.toSet());
                    MolecularCriterium criteria = CkbMolecularCriteriaExtractor.createMolecularCriterium(entry, eventExtractionOutput);

                    if (details.requirementType().equals(VARIANT_REQUIRED)) {
                        requiredCriterium.add(criteria);
                    } else if (details.requirementType().equals(VARIANT_PARTIAL_REQUIRED)) {
                        partiallyRequiredCriterium.add(criteria);
                    }
                }
            }
        }

        if (requiredCriterium.isEmpty() && partiallyRequiredCriterium.isEmpty()) {
            LOGGER.warn("No required criteria found for trial: {}", trial.nctId());
            return null;
        }

        MolecularCriterium combinedRequiredCriterium = CkbMolecularCriteriaExtractor.combine(requiredCriterium);
        Set<MolecularCriterium> anyMolecularCriteria = combinePartialWithRequired(combinedRequiredCriterium, partiallyRequiredCriterium);

        // TODO if we are extracting a variant that represents a combined event, it can already be multiple genes, so the further combining
        //  look messy.
        String allGenes = String.join(GENE_DELIMITER, allVariantWithExtraction.stream()
                .map(g -> g.gene)
                .collect(Collectors.toCollection(TreeSet::new)));

        String allEvents = String.join(EVENT_DELIMITER, allVariantWithExtraction.stream()
                .map(g -> g.event)
                .collect(Collectors.toCollection(TreeSet::new)));

        return actionableTrialFactory.create(trial, anyMolecularCriteria, allGenes, allEvents);
    }

    @VisibleForTesting
    @NotNull
    static Set<MolecularCriterium> combinePartialWithRequired(@NotNull MolecularCriterium requiredCriterium,
            List<MolecularCriterium> partiallyRequiredCriterium) {

        if (partiallyRequiredCriterium.isEmpty()) {
            return Set.of(requiredCriterium);
        } else if (criteriaCount(requiredCriterium) == 0) {
            return partiallyRequiredCriterium.stream().collect(Collectors.toSet());
        } else {
            return Stream.concat(Stream.of(requiredCriterium),
                            partiallyRequiredCriterium.stream().map(partialMolecularCriterium ->
                                    CkbMolecularCriteriaExtractor.combine(requiredCriterium, partialMolecularCriterium)))
                    .collect(Collectors.toSet());
        }

    }

    private void logSummaryStats(@NotNull List<ActionableTrial> trials) {
        List<ActionableTrial> singleCriteriaTrials =
                trials.stream().filter(trial -> trial.anyMolecularCriteria().size() == 1)
                        .collect(Collectors.toList());
        List<ActionableTrial> singleSimpleCriteriaTrials =
                singleCriteriaTrials.stream().filter(trial -> criteriaCount(trial.anyMolecularCriteria().iterator().next()) == 1)
                        .collect(Collectors.toList());

        int totalTrials = trials.size();
        int totalSingleCriteriaTrials = singleCriteriaTrials.size();
        int totalSingleSimpleCriteriaTrials = singleSimpleCriteriaTrials.size();

        LOGGER.info("Total: {} trials", totalTrials);
        LOGGER.info("Trials with single simple criteria: {}", totalSingleSimpleCriteriaTrials);
        LOGGER.info("Trials with single complex criteria: {}", totalSingleCriteriaTrials - totalSingleSimpleCriteriaTrials);
        LOGGER.info("trials with multiple criteria: {}", totalTrials - totalSingleCriteriaTrials);
    }

    private static int criteriaCount(@NotNull MolecularCriterium molecularCriterium) {
        return molecularCriterium.oneOfEachHotspots().size() +
                molecularCriterium.codons().size() +
                molecularCriterium.exons().size() +
                molecularCriterium.genes().size() +
                molecularCriterium.fusions().size() +
                molecularCriterium.characteristics().size() +
                molecularCriterium.hla().size();
    }
}

