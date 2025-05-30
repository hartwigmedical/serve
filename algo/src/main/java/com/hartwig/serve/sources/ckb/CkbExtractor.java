package com.hartwig.serve.sources.ckb;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.hartwig.serve.ckb.classification.CkbConstants;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
import com.hartwig.serve.ckb.classification.CkbEventTypeExtractor;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ExtractionFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableEventExtractorOutput;
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
        ProgressTracker tracker = new ProgressTracker("CKB", entries.size());
        // Assume entries without variants are filtered out prior to extraction
        List<ExtractionResult> extractions = entries.parallelStream()
                .map(this::getExtractionResult)
                .peek(e -> tracker.update())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ExtractionFunctions.merge(extractions);
    }

    @Nullable
    private ExtractionResult getExtractionResult(@NotNull CkbEntry entry) {
        if (entry.variants().isEmpty()) {
            throw new IllegalStateException("A CKB entry without variants has been provided for extraction: " + entry);
        }

        if (entry.type() == EventType.UNKNOWN) {
            LOGGER.warn("No event type known for CKB profile ID '{}': '{}'", entry.profileId(), entry.profileName());
            return null;
        }

        return runExtractionForEntry(entry);
    }

    @NotNull
    private ExtractionResult runExtractionForEntry(@NotNull CkbEntry entry) {
        List<ExtractedEvent> extractedEvents = extractEvents(entry);

        if (extractedEvents.size() != entry.variants().size()) {
            LOGGER.warn("Not all variants could b!e extracted for CKB entry {}: '{}'", entry.profileId(), entry.profileName());
            return extractionWithEventInterpretationOnly(entry, extractedEvents);
        }

        List<ExtractedEvent> eventsWithEmptyExtractorOutput = extractedEvents.stream()
                .filter(event -> countEventExtractorOutputs(event.eventExtractorOutput()) == 0)
                .collect(Collectors.toList());

        if (!eventsWithEmptyExtractorOutput.isEmpty()) {
            if (eventsWithEmptyExtractorOutput.stream().anyMatch(event -> event.eventType() != EventType.COMPLEX)) {
                LOGGER.warn("Not all variants could be extracted for CKB entry {}: '{}'", entry.profileId(), entry.profileName());
            } else {
                LOGGER.debug("Variants with EventType COMPLEX are not extracted for CKB entry {}: '{}'",
                        entry.profileId(),
                        entry.profileName());
            }
            return extractionWithEventInterpretationOnly(entry, extractedEvents);
        }

        if (extractedEvents.stream().anyMatch(event -> event.eventType() == EventType.UNKNOWN)) {
            LOGGER.warn("Not all variants could be extracted for CKB entry {}: '{}'", entry.profileId(), entry.profileName());
            return extractionWithEventInterpretationOnly(entry, extractedEvents);
        }

        EventInterpretation interpretation = interpretEvent(entry, extractedEvents);

        MolecularCriterium molecularCriterium = CkbMolecularCriteriaExtractor.createMolecularCriterium(entry, extractedEvents);

        String combinedEvent = combineEvents(extractedEvents);
        String combinedGenes = combineGenes(extractedEvents);
        Set<EfficacyEvidence> efficacyEvidences = efficacyEvidenceFactory.create(entry, molecularCriterium, combinedEvent, combinedGenes);

        // Only extract trials for simple events for now. Combined events will require more complex handling
        Set<ActionableTrial> actionableTrials = extractedEvents.size() > 1
                ? Set.of()
                : actionableTrialFactory.create(entry, molecularCriterium, combinedEvent, combinedGenes);

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.CKB.refGenomeVersion())
                .eventInterpretations(Set.of(interpretation))
                .knownEvents(CkbKnownEventsExtractor.generateKnownEvents(extractedEvents))
                .evidences(efficacyEvidences)
                .trials(actionableTrials)
                .build();
    }

    @NotNull
    private ExtractionResult extractionWithEventInterpretationOnly(@NotNull CkbEntry entry, @NotNull List<ExtractedEvent> extractedEvents) {
        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.CKB.refGenomeVersion())
                .eventInterpretations(Set.of(interpretEvent(entry, extractedEvents)))
                .build();
    }

    @NotNull
    private EventInterpretation interpretEvent(@NotNull CkbEntry entry, @NotNull List<ExtractedEvent> extractedEvents) {
        if (extractedEvents.isEmpty()) {
            throw new IllegalStateException("No extracted events to interpret");
        } else if (extractedEvents.size() == 1) {
            String gene = extractedEvents.get(0).gene();
            String event = extractedEvents.get(0).event();

            return ImmutableEventInterpretation.builder()
                    .source(Knowledgebase.CKB)
                    .sourceEvent(sourceEvent(gene, event))
                    .interpretedGene(gene)
                    .interpretedEvent(event)
                    .interpretedEventType(entry.type())
                    .build();
        } else {
            return ImmutableEventInterpretation.builder()
                    .source(Knowledgebase.CKB)
                    .sourceEvent("Multiple " + concat(entry.variants()))
                    .interpretedGene("Multiple")
                    .interpretedEvent(concat(entry.variants()))
                    .interpretedEventType(entry.type())
                    .build();
        }
    }

    @NotNull
    private static String sourceEvent(@NotNull String gene, @NotNull String event) {
        return gene.equals(CkbConstants.NO_GENE) ? event : gene + " " + event;
    }

    @NotNull
    private static String combineEvents(@NotNull List<ExtractedEvent> events) {
        return events.stream()
                .map(e -> {
                    String event = e.event();
                    String gene = e.gene();
                    return sourceEvent(gene, event);
                })
                .collect(Collectors.joining(VARIANT_DELIMITER));
    }

    @NotNull
    private static String combineGenes(@NotNull List<ExtractedEvent> events) {
        return events.stream()
                .map(ExtractedEvent::gene)
                .collect(Collectors.joining(GENE_DELIMITER));
    }

    @NotNull
    private List<ExtractedEvent> extractEvents(@NotNull CkbEntry entry) {
        return entry.variants().stream()
                .map(this::extractEvent)
                .collect(Collectors.toList());
    }

    @NotNull
    private ExtractedEvent extractEvent(@NotNull Variant variant) {
        EventType eventType = CkbEventTypeExtractor.classify(variant);

        if (eventType == EventType.COMBINED) {
            throw new IllegalStateException("Should not have combined event for single variant: " + variant.fullName());
        }

        String gene = CkbEventAndGeneExtractor.extractGene(variant);
        String event = CkbEventAndGeneExtractor.extractEvent(variant);

        ImmutableExtractedEvent.Builder extractedEventBuilder = ImmutableExtractedEvent.builder()
                .gene(gene)
                .event(event)
                .variant(variant)
                .eventType(eventType);

        if (eventType == EventType.UNKNOWN) {
            LOGGER.warn("No known event type for variant: '{}'", variant.fullName());
            return extractedEventBuilder
                    .eventExtractorOutput(ImmutableEventExtractorOutput.builder().build())
                    .build();
        }

        EventExtractorOutput eventExtractorOutput =
                CkbMolecularCriteriaExtractor.curateCodons(eventExtractor.extract(gene, null, eventType, event));

        return extractedEventBuilder
                .eventExtractorOutput(eventExtractorOutput)
                .build();
    }

    @NotNull
    private static String concat(@NotNull List<Variant> variants) {
        return variants.stream().map(Variant::variant).collect(Collectors.joining(VARIANT_DELIMITER));
    }

    private int countEventExtractorOutputs(@NotNull EventExtractorOutput eventExtractorOutput) {
        return (eventExtractorOutput.variants() != null ? eventExtractorOutput.variants().size() : 0) +
                (eventExtractorOutput.codons() != null ? eventExtractorOutput.codons().size() : 0) +
                (eventExtractorOutput.exons() != null ? eventExtractorOutput.exons().size() : 0) +
                (eventExtractorOutput.geneLevel() != null ? 1 : 0) +
                (eventExtractorOutput.copyNumber() != null ? 1 : 0) +
                (eventExtractorOutput.fusionPair() != null ? 1 : 0) +
                (eventExtractorOutput.characteristic() != null ? 1 : 0) +
                (eventExtractorOutput.hla() != null ? 1 : 0);
    }
}

