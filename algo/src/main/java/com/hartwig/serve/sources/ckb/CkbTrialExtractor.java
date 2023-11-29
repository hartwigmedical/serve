package com.hartwig.serve.sources.ckb;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.classification.CkbConstants;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.extraction.ActionableEventFactory;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ExtractionFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.events.ImmutableEventInterpretation;
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbTrialExtractor {

    private static final Logger LOGGER = LogManager.getLogger(CkbTrialExtractor.class);
    private static final String DELIMITER = ",";

    @NotNull
    private final EventExtractor eventExtractor;

    CkbTrialExtractor(@NotNull final EventExtractor eventExtractor) {
        this.eventExtractor = eventExtractor;
    }

    @NotNull
    public ExtractionResult extract(@NotNull List<CkbEntry> entries) {
        List<ExtractionResult> extractions = Lists.newArrayList();

        ProgressTracker tracker = new ProgressTracker("CKB Trials", entries.size());
        for (CkbEntry entry : entries) {
            // Assume entries without variants are filtered out prior to extraction
            if (entry.variants().isEmpty()) {
                throw new IllegalStateException("A CKB entry without variants has been provided for extraction: " + entry);
            }

            int variantCount = entry.variants().size();
            Variant variant = entry.variants().get(0);
            String event = variantCount > 1 ? concat(entry.variants()) : CkbEventAndGeneExtractor.extractEvent(variant);
            String gene = variantCount > 1 ? "Multiple" : CkbEventAndGeneExtractor.extractGene(variant);

            if (entry.type() == EventType.UNKNOWN) {
                LOGGER.warn("No event type known for '{}' on '{}'", event, gene);
            } else {
                EventExtractorOutput extractionOutput = eventExtractor.extract(gene, null, entry.type(), event);
                String sourceEvent;
                if (!gene.equals(CkbConstants.NO_GENE)) {
                    sourceEvent = gene + " " + event;
                } else {
                    sourceEvent = event;
                }

                Set<ActionableEntry> actionableTrials =
                        ActionableTrialFactory.toActionableTrials(entry, sourceEvent);

                EventInterpretation interpretation = ImmutableEventInterpretation.builder()
                        .source(Knowledgebase.CKB_TRIAL)
                        .sourceEvent(sourceEvent)
                        .interpretedGene(gene)
                        .interpretedEvent(event)
                        .interpretedEventType(entry.type())
                        .build();

                ExtractionResult extraction = toExtractionResult(event, gene, null, extractionOutput, actionableTrials, interpretation);
                extractions.add(extraction);
            }

            tracker.update();
        }

        return ExtractionFunctions.merge(extractions);
    }

    @NotNull
    private static String concat(@NotNull List<Variant> variants) {
        StringJoiner joiner = new StringJoiner(DELIMITER);
        for (Variant variant : variants) {
            joiner.add(variant.variant());
        }
        return joiner.toString();
    }

    @NotNull
    private static ExtractionResult toExtractionResult(@NotNull String variant, @NotNull String gene, @Nullable String transcript,
            @NotNull EventExtractorOutput output, @NotNull Set<ActionableEntry> actionableTrials,
            @NotNull EventInterpretation interpretation) {
        Set<ActionableHotspot> actionableHotspots = Sets.newHashSet();
        Set<ActionableRange> actionableCodons = Sets.newHashSet();
        Set<ActionableRange> actionableExons = Sets.newHashSet();
        Set<ActionableGene> actionableGenes = Sets.newHashSet();
        Set<ActionableFusion> actionableFusions = Sets.newHashSet();
        Set<ActionableCharacteristic> actionableCharacteristics = Sets.newHashSet();
        Set<ActionableHLA> actionableHLA = Sets.newHashSet();

        List<CodonAnnotation> codons = Lists.newArrayList();

        for (ActionableEntry trial : actionableTrials) {
            codons = curateCodons(output.codons());

            actionableHotspots.addAll(ActionableEventFactory.toActionableHotspots(trial, output.hotspots()));
            actionableCodons.addAll(ActionableEventFactory.toActionableRanges(trial, codons));
            actionableExons.addAll(ActionableEventFactory.toActionableRanges(trial, output.exons()));

            if (output.geneLevel() != null) {
                actionableGenes.add(ActionableEventFactory.geneAnnotationToActionableGene(trial, output.geneLevel()));
            }

            if (output.copyNumber() != null) {
                actionableGenes.add(ActionableEventFactory.geneAnnotationToActionableGene(trial, output.copyNumber()));
            }

            if (output.fusionPair() != null) {
                actionableFusions.add(ActionableEventFactory.toActionableFusion(trial, output.fusionPair()));
            }

            if (output.characteristic() != null) {
                actionableCharacteristics.add(ActionableEventFactory.toActionableCharacteristic(trial, output.characteristic()));
            }

            if (output.hla() != null) {
                actionableHLA.add(ActionableEventFactory.toActionableHLa(trial, output.hla()));
            }
        }

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.CKB_EVIDENCE.refGenomeVersion())
                .addEventInterpretations(interpretation)
                .actionableHotspots(actionableHotspots)
                .actionableCodons(actionableCodons)
                .actionableExons(actionableExons)
                .actionableGenes(actionableGenes)
                .actionableFusions(actionableFusions)
                .actionableCharacteristics(actionableCharacteristics)
                .actionableHLA(actionableHLA)
                .build();
    }

    @VisibleForTesting
    @Nullable
    static List<CodonAnnotation> curateCodons(@Nullable List<CodonAnnotation> codonAnnotations) {
        if (codonAnnotations == null) {
            return null;
        }

        List<CodonAnnotation> curatedCodons = Lists.newArrayList();
        for (CodonAnnotation codon : codonAnnotations) {
            if (codon.gene().equals("BRAF") && codon.inputCodonRank() == 600) {
                curatedCodons.add(ImmutableCodonAnnotation.builder()
                        .from(codon)
                        .inputTranscript("ENST00000646891")
                        .start(140753335)
                        .end(140753337)
                        .build());
            } else {
                curatedCodons.add(codon);
            }
        }
        return curatedCodons;
    }
}