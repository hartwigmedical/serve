package com.hartwig.serve.sources.ckbtrial;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
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
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.events.ImmutableEventInterpretation;
import com.hartwig.serve.iclusion.classification.IclusionConstants;
import com.hartwig.serve.iclusion.datamodel.IclusionMutation;
import com.hartwig.serve.iclusion.datamodel.IclusionMutationCondition;
import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class CkbTrialExtractor {

    private static final Logger LOGGER = LogManager.getLogger(CkbTrialExtractor.class);

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
//            if (entry.clinicalTrials().isEmpty()) {
//                throw new IllegalStateException("A CKB entry without clinical trials has been provided for extraction: " + entry);
//            }

            for (ClinicalTrial trial : entry.clinicalTrials()) {
                for (VariantRequirementDetail variantRequirementDetail : trial.variantRequirementDetails()) {
//                    for (IclusionMutation mutation : mutationCondition.mutations()) {
//                        LOGGER.debug("Interpreting '{}' on '{}' for {}", mutation.name(), mutation.gene(), trial.nctId());
//
//                        if (mutation.type() == EventType.UNKNOWN) {
//                            LOGGER.warn("No event type known for '{}' on '{}'", mutation.name(), mutation.gene());
//                        } else {
//                            extractions.add(extract(trial, mutation));
//                        }
//                    }
                }

                tracker.update();
            }
        }

        return ExtractionFunctions.merge(extractions);
    }

//    @NotNull
//    private ExtractionResult extract(@NotNull IclusionTrial trial, @NotNull IclusionMutation mutation) {
//        String sourceEvent;
//        if (!mutation.gene().equals(IclusionConstants.NO_GENE)) {
//            sourceEvent = mutation.gene() + " " + mutation.name();
//        } else {
//            sourceEvent = mutation.name();
//        }
//
//        List<ActionableTrial> actionableTrials = actionableTrialFactory.toActionableTrials(trial, sourceEvent);
//        for (ActionableTrial actionableTrial : actionableTrials) {
//            LOGGER.debug("Generated {} based off {}", actionableTrial, trial);
//        }
//
//        EventExtractorOutput extraction = eventExtractor.extract(mutation.gene(), null, mutation.type(), mutation.name());
//
//        EventInterpretation interpretation = ImmutableEventInterpretation.builder()
//                .source(Knowledgebase.ICLUSION)
//                .sourceEvent(sourceEvent)
//                .interpretedGene(mutation.gene())
//                .interpretedEvent(mutation.name())
//                .interpretedEventType(mutation.type())
//                .build();
//
//        return toExtractionResult(actionableTrials, extraction, interpretation);
//    }
//
//    @NotNull
//    private static ExtractionResult toExtractionResult(@NotNull List<ActionableTrial> actionableTrials,
//            @NotNull EventExtractorOutput extraction, @NotNull EventInterpretation interpretation) {
//        Set<ActionableHotspot> actionableHotspots = Sets.newHashSet();
//        Set<ActionableRange> actionableCodons = Sets.newHashSet();
//        Set<ActionableRange> actionableExons = Sets.newHashSet();
//        Set<ActionableGene> actionableGenes = Sets.newHashSet();
//        Set<ActionableFusion> actionableFusions = Sets.newHashSet();
//        Set<ActionableCharacteristic> actionableCharacteristics = Sets.newHashSet();
//        Set<ActionableHLA> actionableHLA = Sets.newHashSet();
//
//        for (ActionableTrial trial : actionableTrials) {
//            actionableHotspots.addAll(ActionableEventFactory.toActionableHotspots(trial, extraction.hotspots()));
//            actionableCodons.addAll(ActionableEventFactory.toActionableRanges(trial, extraction.codons()));
//            actionableExons.addAll(ActionableEventFactory.toActionableRanges(trial, extraction.exons()));
//
//            if (extraction.geneLevel() != null) {
//                actionableGenes.add(ActionableEventFactory.geneAnnotationToActionableGene(trial, extraction.geneLevel()));
//            }
//
//            if (extraction.copyNumber() != null) {
//                actionableGenes.add(ActionableEventFactory.geneAnnotationToActionableGene(trial, extraction.copyNumber()));
//            }
//
//            if (extraction.fusionPair() != null) {
//                actionableFusions.add(ActionableEventFactory.toActionableFusion(trial, extraction.fusionPair()));
//            }
//
//            if (extraction.characteristic() != null) {
//                actionableCharacteristics.add(ActionableEventFactory.toActionableCharacteristic(trial, extraction.characteristic()));
//            }
//
//            if (extraction.hla() != null) {
//                actionableHLA.add(ActionableEventFactory.toActionableHLa(trial, extraction.hla()));
//            }
//        }
//
//        return ImmutableExtractionResult.builder()
//                .refGenomeVersion(Knowledgebase.ICLUSION.refGenomeVersion())
//                .addEventInterpretations(interpretation)
//                .actionableHotspots(actionableHotspots)
//                .actionableCodons(actionableCodons)
//                .actionableExons(actionableExons)
//                .actionableGenes(actionableGenes)
//                .actionableFusions(actionableFusions)
//                .actionableCharacteristics(actionableCharacteristics)
//                .actionableHLA(actionableHLA)
//                .build();
//    }
}