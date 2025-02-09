package com.hartwig.serve.sources.ckb;

import static com.hartwig.serve.sources.ckb.CkbVariantCriteriaExtractor.curateCodons;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.serve.ckb.classification.CkbConstants;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
import com.hartwig.serve.ckb.classification.CkbEventTypeExtractor;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.ImmutableActionableEventImpl;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.molecular.ActionableEvent;
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

    @NotNull
    private final EventExtractor eventExtractor;
    @NotNull
    private final EfficacyEvidenceFactory efficacyEvidenceFactory;
    @NotNull
    private final ActionableTrialFactory actionableTrialFactory;
    @NotNull
    private final CkbMolecularCriteriaExtractor molecularCriteriaExtractor;

    CkbExtractor(@NotNull final EventExtractor eventExtractor, @NotNull EfficacyEvidenceFactory efficacyEvidenceFactory,
            @NotNull ActionableTrialFactory actionableTrialFactory) {
        this.eventExtractor = eventExtractor;
        this.efficacyEvidenceFactory = efficacyEvidenceFactory;
        this.actionableTrialFactory = actionableTrialFactory;
        this.molecularCriteriaExtractor = new CkbMolecularCriteriaExtractor(this.eventExtractor); // TODO inject, I guess
    }

    @NotNull
    public ExtractionResult extract(@NotNull List<CkbEntry> entries) {

        LOGGER.info("total number of ckb entries: {}", entries.size());

        // test call into new functionality for trials, TODO needs to be reworked into actual flow
        List<ActionableTrial> alternativeUniverseNewTrials = processTrials(entries);
        LOGGER.info("total number of alternative trials: {}", alternativeUniverseNewTrials.size());

        ProgressTracker tracker = new ProgressTracker("CKB", entries.size());
        // Assume entries without variants are filtered out prior to extraction
        List<ExtractionResult> extractions = entries.parallelStream()
                .map(this::getExtractionResult)
                .peek(e -> tracker.update())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        ExtractionResult origResult = ExtractionFunctions.merge(extractions);
        ExtractionResult resultWithCombined =
                ImmutableExtractionResult.builder()
                        .from(origResult)
                        .trials(alternativeUniverseNewTrials)
                        .build();
        LOGGER.info("orig result contained {} trials", origResult.trials() != null ? origResult.trials().size() : 0);
        LOGGER.info("new result with combined evidence contains {} trials",
                resultWithCombined.trials() != null ? resultWithCombined.trials().size() : 0);
        return resultWithCombined;
    }

    @Nullable
    private ExtractionResult getExtractionResult(@NotNull CkbEntry entry) {
        if (entry.variants().isEmpty()) {
            throw new IllegalStateException("A CKB entry without variants has been provided for extraction: " + entry);
        }
        int variantCount = entry.variants().size();
        Variant variant = entry.variants().get(0);
        String event = variantCount > 1 ? concat(entry.variants()) : CkbEventAndGeneExtractor.extractEvent(variant);
        String gene = variantCount > 1 ? "Multiple" : CkbEventAndGeneExtractor.extractGene(variant);

        if (entry.type() == EventType.UNKNOWN) {
            LOGGER.warn("No event type known for '{}' on '{}'", event, gene);
            return null;
        } else {
            // why is curateCodons here? TODO refactoring borken (moved)
            EventExtractorOutput extractionOutput = curateCodons(eventExtractor.extract(gene, null, entry.type(), event));
            String sourceEvent = gene.equals(CkbConstants.NO_GENE) ? event : gene + " " + event;

            EventInterpretation interpretation = ImmutableEventInterpretation.builder()
                    .source(Knowledgebase.CKB)
                    .sourceEvent(sourceEvent)
                    .interpretedGene(gene)
                    .interpretedEvent(event)
                    .interpretedEventType(entry.type())
                    .build();

            Set<EventExtractorOutput> eventExtractionOutput = extractCriteria(entry);
            MolecularCriterium molecularCriterium = this.molecularCriteriaExtractor.criterium(entry, eventExtractionOutput);

            //            MolecularCriterium theNewWayMolecualrCriteria = this.molecularCriteriaExtractor.criteriumForEntry(entry);
            //            thing = extractCriteria(entry);

            //            Set<EfficacyEvidence> efficacyEvidences = efficacyEvidenceFactory.create(entry, molecularCriteria, sourceEvent, gene);
            Set<EfficacyEvidence> efficacyEvidences =
                    efficacyEvidenceFactory.create(entry, Set.of(molecularCriterium), sourceEvent, gene);
            //            Set<ActionableTrial> actionableTrials = actionableTrialFactory.create(entry, molecularCriteria, sourceEvent, gene);

            return ImmutableExtractionResult.builder()
                    .refGenomeVersion(Knowledgebase.CKB.refGenomeVersion())
                    .eventInterpretations(Set.of(interpretation))
                    .knownEvents(CkbKnownEventsGenerator.generateKnownEvents(extractionOutput,
                            efficacyEvidences.isEmpty(),
                            variant,
                            event,
                            gene))
                    .evidences(efficacyEvidences)
                    //                    .trials(actionableTrials)
                    .trials(Set.of())  // replaced elsewhere
                    .build();
        }
    }

    @Nullable
    public Set<EventExtractorOutput> extractCriteria(@NotNull CkbEntry entry) {
        String sourceEvent = combinedSourceEvent(entry);
        ActionableEvent actionableEvent = toActionableEvent(sourceEvent, entry);

        return entry.variants().stream()
                .map(variant -> extractCriteria(variant, actionableEvent))
                .filter(Objects::nonNull) // TODO should we bail out here or use the rest?
                .collect(Collectors.toSet());

    }

    @Nullable
    public EventExtractorOutput extractCriteria(@NotNull Variant variant, ActionableEvent actionableEvent) {
        EventType eventType = CkbEventTypeExtractor.classify(variant);

        if (eventType == EventType.COMBINED) {
            throw new IllegalStateException("Should not have combined event for single variant: " + variant.fullName());
        } else if (eventType == EventType.UNKNOWN) {
            LOGGER.warn("No known event type for variant: '{}'", variant.fullName());
            return null;
        }

        String event = CkbEventAndGeneExtractor.extractEvent(variant);
        String gene = CkbEventAndGeneExtractor.extractGene(variant);

        EventExtractorOutput extractionOutput = curateCodons(eventExtractor.extract(gene, null, eventType, event));
        return extractionOutput;
    }

    @NotNull
    private String combinedSourceEvent(@NotNull CkbEntry entry) {
        return entry.variants().stream()
                .map(variant -> {
                    String event = CkbEventAndGeneExtractor.extractEvent(variant);
                    String gene = CkbEventAndGeneExtractor.extractGene(variant);
                    return gene.equals(CkbConstants.NO_GENE) ? event : gene + " " + event;
                })
                .collect(Collectors.joining(" & "));
    }

    @NotNull
    private static ActionableEvent toActionableEvent(@NotNull String sourceEvent, @NotNull CkbEntry entry) {
        String sourceUrl = "https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=" + entry.profileId();
        LocalDate sourceDate = entry.createDate();
        return ImmutableActionableEventImpl.builder().sourceDate(sourceDate).sourceEvent(sourceEvent).sourceUrls(Set.of(sourceUrl)).build();
    }

    @NotNull
    private static String concat(@NotNull List<Variant> variants) {
        return variants.stream().map(Variant::variant).collect(Collectors.joining(VARIANT_DELIMITER));
    }

    // trial stuff
    public List<ActionableTrial> processTrials(@NotNull List<CkbEntry> ckbEntries) {
        Map<Integer, CkbEntry> idToEntry = ckbEntries.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(CkbEntry::profileId, entry -> entry),
                        Collections::unmodifiableMap));

        Set<ClinicalTrial> uniqueTrials = ckbEntries.stream()
                .flatMap(entry -> entry.clinicalTrials().stream())
                .collect(Collectors.toSet());

        // TODO does parallelism here make sense?
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

        for (VariantRequirementDetail details : trial.variantRequirementDetails()) {
            Integer profileId = details.profileId();
            if (!idToEntry.containsKey(profileId)) {
                throw new IllegalStateException("No entry found for profile ID: " + profileId);
            }

            CkbEntry entry = idToEntry.get(profileId);
            Set<EventExtractorOutput> eventExtractionOutput = extractCriteria(entry);

            MolecularCriterium criteria = this.molecularCriteriaExtractor.criterium(entry, eventExtractionOutput);

            if (details.requirementType().equals("required")) {
                requiredCriterium.add(criteria);
            } else if (details.requirementType().equals("partial - required")) {
                partiallyRequiredCriterium.add(criteria);
            }
        }

        if (requiredCriterium.isEmpty() && partiallyRequiredCriterium.isEmpty()) {
            LOGGER.warn("No required criteria found for trial: {}", trial.nctId());
            return null;
        }

        MolecularCriterium combinedRequiredCriterium = MolecularCriteriaCombiner.combine(requiredCriterium);
        Set<MolecularCriterium> anyMolecularCriteria = combinePartialWithRequired(combinedRequiredCriterium, partiallyRequiredCriterium);
        return actionableTrialFactory.createV2(anyMolecularCriteria, trial);
    }

    @VisibleForTesting
    @NotNull
    static Set<MolecularCriterium> combinePartialWithRequired(MolecularCriterium requiredCriterium,
            List<MolecularCriterium> partiallyRequiredCriterium) {

        if (partiallyRequiredCriterium.isEmpty()) {
            return Set.of(requiredCriterium);
        } else {
            return partiallyRequiredCriterium.stream().map(partialMolecularCriterium ->
                            MolecularCriteriaCombiner.combine(requiredCriterium, partialMolecularCriterium))
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

    private int criteriaCount(@NotNull MolecularCriterium molecularCriterium) {
        return molecularCriterium.oneOfEachHotspots().size() +
                molecularCriterium.codons().size() +
                molecularCriterium.exons().size() +
                molecularCriterium.genes().size() +
                molecularCriterium.fusions().size() +
                molecularCriterium.characteristics().size() +
                molecularCriterium.hla().size();
    }

}

