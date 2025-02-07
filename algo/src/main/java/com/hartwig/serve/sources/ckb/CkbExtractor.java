package com.hartwig.serve.sources.ckb;

import static com.hartwig.serve.sources.ckb.CkbVariantCriteriaExtractor.curateCodons;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.hartwig.serve.ckb.classification.CkbConstants;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
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
        CkbTrialExtractor ckbTrialExtractor = new CkbTrialExtractor(this.actionableTrialFactory, molecularCriteriaExtractor);
        List<ActionableTrial> alternativeUniverseNewTrials = ckbTrialExtractor.processTrials(entries);
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

            MolecularCriterium theNewWayMolecualrCriteria = this.molecularCriteriaExtractor.criteriumForEntry(entry);

            //            Set<EfficacyEvidence> efficacyEvidences = efficacyEvidenceFactory.create(entry, molecularCriteria, sourceEvent, gene);
            Set<EfficacyEvidence> efficacyEvidences =
                    efficacyEvidenceFactory.create(entry, Set.of(theNewWayMolecualrCriteria), sourceEvent, gene);
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

    @NotNull
    private static String concat(@NotNull List<Variant> variants) {
        return variants.stream().map(Variant::variant).collect(Collectors.joining(VARIANT_DELIMITER));
    }
}

