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
import com.hartwig.serve.extraction.ExtractedEvent;
import com.hartwig.serve.extraction.ExtractionFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractedEvent;
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

        if (entry.variants().size() == 1) {
            return extractSimpleEvent(entry);
        } else {
            return extractCombinedEvent(entry);
        }
    }

    @NotNull
    private ExtractionResult extractSimpleEvent(@NotNull CkbEntry entry) {
        Variant variant = entry.variants().get(0);
        String gene = CkbEventAndGeneExtractor.extractGene(variant);
        String event = CkbEventAndGeneExtractor.extractEvent(variant);

        String sourceEvent = gene.equals(CkbConstants.NO_GENE) ? event : gene + " " + event;

        EventInterpretation interpretation = ImmutableEventInterpretation.builder()
                .source(Knowledgebase.CKB)
                .sourceEvent(sourceEvent)
                .interpretedGene(gene)
                .interpretedEvent(event)
                .interpretedEventType(entry.type())
                .build();

        List<ExtractedEvent> extractedEvents = extractEvents(entry);
        Set<MolecularCriterium> molecularCriteria = Set.of(CkbMolecularCriteriaExtractor.createMolecularCriterium(entry, extractedEvents));

        Set<EfficacyEvidence> efficacyEvidences = efficacyEvidenceFactory.create(entry, molecularCriteria, sourceEvent, gene);
        Set<ActionableTrial> actionableTrials = actionableTrialFactory.create(entry, molecularCriteria, sourceEvent, gene);

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.CKB.refGenomeVersion())
                .eventInterpretations(Set.of(interpretation))
                .knownEvents(CkbKnownEventsExtractor.generateKnownEvents(extractedEvents, efficacyEvidences.isEmpty()))
                .evidences(efficacyEvidences)
                .trials(actionableTrials)
                .build();
    }

    @NotNull
    private ExtractionResult extractCombinedEvent(@NotNull CkbEntry entry) {

        EventInterpretation interpretation = ImmutableEventInterpretation.builder()
                .source(Knowledgebase.CKB)
                .sourceEvent("Multiple " + concat(entry.variants()))
                .interpretedGene("Multiple")
                .interpretedEvent(concat(entry.variants()))
                .interpretedEventType(entry.type())
                .build();

        List<ExtractedEvent> extractedEvents = extractEvents(entry);
        String combinedEvent = combineEvents(extractedEvents);
        String combinedGenes = combineGenes(extractedEvents);

        MolecularCriterium molecularCriterium = CkbMolecularCriteriaExtractor.createMolecularCriterium(entry, extractedEvents);

        Set<EfficacyEvidence> efficacyEvidences =
                efficacyEvidenceFactory.create(entry, Set.of(molecularCriterium), combinedEvent, combinedGenes);

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.CKB.refGenomeVersion())
                .eventInterpretations(Set.of(interpretation))
                .knownEvents(CkbKnownEventsExtractor.generateKnownEvents(extractedEvents, efficacyEvidences.isEmpty()))
                .evidences(efficacyEvidences)
                .trials(Set.of())
                .build();
    }

    @NotNull
    private static String combineEvents(@NotNull List<ExtractedEvent> events) {
        return events.stream()
                .map(e -> {
                    String event = e.event();
                    String gene = e.gene();
                    return gene.equals(CkbConstants.NO_GENE) ? event : gene + " " + event;
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
        // TODO if one event returns null, we are still returning the others. Should we reject the whole entry?
        return entry.variants().stream()
                .map(this::extractEvent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Nullable
    private ExtractedEvent extractEvent(@NotNull Variant variant) {
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

        return ImmutableExtractedEvent.builder()
                .gene(gene)
                .event(event)
                .variant(variant)
                .eventType(eventType)
                .eventExtractorOutput(eventExtractorOutput)
                .build();
    }

    @NotNull
    private static String concat(@NotNull List<Variant> variants) {
        return variants.stream().map(Variant::variant).collect(Collectors.joining(VARIANT_DELIMITER));
    }
}

