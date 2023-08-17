package com.hartwig.serve.sources.vicc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.FusionPair;
import com.hartwig.serve.datamodel.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.range.*;
import com.hartwig.serve.extraction.ActionableEventFactory;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ExtractionFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.CodonConsolidation;
import com.hartwig.serve.extraction.copynumber.CopyNumberConsolidation;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.events.ImmutableEventInterpretation;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.exon.ExonConsolidation;
import com.hartwig.serve.extraction.fusion.FusionConsolidation;
import com.hartwig.serve.extraction.hotspot.HotspotConsolidation;
import com.hartwig.serve.extraction.immuno.ImmunoHLA;
import com.hartwig.serve.util.ProgressTracker;
import com.hartwig.serve.vicc.annotation.ViccProteinAnnotationExtractor;
import com.hartwig.serve.vicc.datamodel.Feature;
import com.hartwig.serve.vicc.datamodel.ViccEntry;
import com.hartwig.serve.vicc.datamodel.ViccSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ViccExtractor {

    private static final Logger LOGGER = LogManager.getLogger(ViccExtractor.class);

    @NotNull
    private final EventExtractor eventExtractor;
    @NotNull
    private final ActionableEvidenceFactory actionableEvidenceFactory;

    public ViccExtractor(@NotNull final EventExtractor eventExtractor, @NotNull final ActionableEvidenceFactory actionableEvidenceFactory) {
        this.eventExtractor = eventExtractor;
        this.actionableEvidenceFactory = actionableEvidenceFactory;
    }

    @NotNull
    public ExtractionResult extract(@NotNull List<ViccEntry> entries) {
        Map<ViccEntry, ViccExtractionResult> resultsPerEntry = Maps.newHashMap();
        List<ExtractionResult> extractions = Lists.newArrayList();

        ProgressTracker tracker = new ProgressTracker("VICC", entries.size());
        for (ViccEntry entry : entries) {
            ViccExtractionResult extraction = extractEntry(entry);
            resultsPerEntry.put(entry, extraction);
            extractions.add(toExtractionResult(entry, extraction));

            tracker.update();
        }

        actionableEvidenceFactory.evaluateCuration();
        ViccUtil.printExtractionResults(resultsPerEntry);
        return ExtractionFunctions.merge(extractions);
    }

    @NotNull
    private ViccExtractionResult extractEntry(@NotNull ViccEntry entry) {
        Map<Feature, EventInterpretation> eventInterpretationPerFeature = Maps.newHashMap();
        Map<Feature, List<VariantHotspot>> hotspotsPerFeature = Maps.newHashMap();
        Map<Feature, List<CodonAnnotation>> codonsPerFeature = Maps.newHashMap();
        Map<Feature, List<ExonAnnotation>> exonsPerFeature = Maps.newHashMap();
        Map<Feature, GeneAnnotation> geneLevelEventsPerFeature = Maps.newHashMap();
        Map<Feature, GeneAnnotation> ampsDelsPerFeature = Maps.newHashMap();
        Map<Feature, FusionPair> fusionsPerFeature = Maps.newHashMap();
        Map<Feature, TumorCharacteristic> characteristicsPerFeature = Maps.newHashMap();
        Map<Feature, ImmunoHLA> hlaPerFeature = Maps.newHashMap();

        for (Feature feature : entry.features()) {
            String gene = feature.geneSymbol();
            if (gene == null) {
                LOGGER.warn("No gene configured for {}. Skipping!", feature);
            } else {
                EventExtractorOutput extractorOutput = eventExtractor.extract(gene, entry.transcriptId(), feature.type(), feature.name());

                String sourceEvent = feature.name().startsWith(gene) ? feature.name() : gene + " " + feature.name();
                eventInterpretationPerFeature.put(feature,
                        ImmutableEventInterpretation.builder()
                                .source(ActionableEvidenceFactory.fromViccSource(entry.source()))
                                .sourceEvent(sourceEvent)
                                .interpretedGene(gene)
                                .interpretedEvent(feature.name())
                                .interpretedEventType(feature.type())
                                .build());

                if (extractorOutput.hotspots() != null) {
                    hotspotsPerFeature.put(feature, extractorOutput.hotspots());
                }

                if (extractorOutput.codons() != null) {
                    codonsPerFeature.put(feature, extractorOutput.codons());
                }

                if (extractorOutput.exons() != null) {
                    exonsPerFeature.put(feature, extractorOutput.exons());
                }

                if (extractorOutput.geneLevel() != null) {
                    geneLevelEventsPerFeature.put(feature, extractorOutput.geneLevel());
                }

                if (extractorOutput.copyNumber() != null) {
                    ampsDelsPerFeature.put(feature, extractorOutput.copyNumber());
                }

                if (extractorOutput.fusionPair() != null) {
                    fusionsPerFeature.put(feature, extractorOutput.fusionPair());
                }

                if (extractorOutput.characteristic() != null) {
                    characteristicsPerFeature.put(feature, extractorOutput.characteristic());
                }

                if (extractorOutput.hla() != null) {
                    hlaPerFeature.put(feature, extractorOutput.hla());
                }
            }
        }

        return ImmutableViccExtractionResult.builder()
                .eventInterpretationPerFeature(eventInterpretationPerFeature)
                .hotspotsPerFeature(hotspotsPerFeature)
                .codonsPerFeature(codonsPerFeature)
                .exonsPerFeature(exonsPerFeature)
                .geneLevelEventsPerFeature(geneLevelEventsPerFeature)
                .ampsDelsPerFeature(ampsDelsPerFeature)
                .fusionsPerFeature(fusionsPerFeature)
                .characteristicsPerFeature(characteristicsPerFeature)
                .HLAPerFeature(hlaPerFeature)
                .actionableEvidence(actionableEvidenceFactory.toActionableEvidence(entry))
                .build();
    }

    @NotNull
    private static ExtractionResult toExtractionResult(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        // Assume all VICC knowledgebases are on the same ref genome version
        ImmutableExtractionResult.Builder outputBuilder = ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.VICC_CGI.refGenomeVersion())
                .addAllEventInterpretations(extraction.eventInterpretationPerFeature().values())
                .knownHotspots(convertToHotspots(entry, extraction))
                .knownCodons(convertToCodons(entry, extraction))
                .knownExons(convertToExons(entry, extraction))
                .knownCopyNumbers(convertToKnownCopyNumbers(entry, extraction))
                .knownFusions(convertToKnownFusions(entry, extraction));

        addActionability(outputBuilder, extraction);

        return outputBuilder.build();
    }

    @NotNull
    private static Set<KnownHotspot> convertToHotspots(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        ViccProteinAnnotationExtractor proteinExtractor = new ViccProteinAnnotationExtractor();
        Set<KnownHotspot> hotspots = Sets.newHashSet();
        Knowledgebase source = ViccSource.toKnowledgebase(entry.source());
        for (Map.Entry<Feature, List<VariantHotspot>> featureResult : extraction.hotspotsPerFeature().entrySet()) {
            Feature feature = featureResult.getKey();
            for (VariantHotspot hotspot : featureResult.getValue()) {
                hotspots.add(ImmutableKnownHotspot.builder()
                        .from(hotspot)
                        .geneRole(GeneRole.UNKNOWN)
                        .proteinEffect(ProteinEffect.UNKNOWN)
                        .inputTranscript(entry.transcriptId())
                        .inputProteinAnnotation(proteinExtractor.apply(feature.name()))
                        .addSources(source)
                        .build());
            }
        }

        return HotspotConsolidation.consolidate(hotspots);
    }

    @NotNull
    private static Set<KnownCodon> convertToCodons(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        Set<KnownCodon> codons = Sets.newHashSet();

        Knowledgebase source = ViccSource.toKnowledgebase(entry.source());
        for (List<CodonAnnotation> annotations : extraction.codonsPerFeature().values()) {
            for (CodonAnnotation annotation : annotations) {
                codons.add(ImmutableKnownCodon.builder()
                        .from(annotation)
                        .geneRole(GeneRole.UNKNOWN)
                        .proteinEffect(ProteinEffect.UNKNOWN)
                        .inputTranscript(annotation.inputTranscript())
                        .inputCodonRank(annotation.inputCodonRank())
                        .addSources(source)
                        .build());
            }
        }

        return CodonConsolidation.consolidate(codons);
    }

    @NotNull
    private static Set<KnownExon> convertToExons(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        Set<KnownExon> exons = Sets.newHashSet();

        Knowledgebase source = ViccSource.toKnowledgebase(entry.source());
        for (List<ExonAnnotation> annotations : extraction.exonsPerFeature().values()) {
            for (ExonAnnotation annotation : annotations) {
                exons.add(ImmutableKnownExon.builder()
                        .from(annotation)
                        .geneRole(GeneRole.UNKNOWN)
                        .proteinEffect(ProteinEffect.UNKNOWN)
                        .inputTranscript(annotation.inputTranscript())
                        .inputExonRank(annotation.inputExonRank())
                        .addSources(source)
                        .build());
            }
        }

        return ExonConsolidation.consolidate(exons);
    }

    @NotNull
    private static Set<KnownCopyNumber> convertToKnownCopyNumbers(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        Set<KnownCopyNumber> copyNumbers = Sets.newHashSet();
        Knowledgebase source = ViccSource.toKnowledgebase(entry.source());
        for (GeneAnnotation copyNumber : extraction.ampsDelsPerFeature().values()) {
            copyNumbers.add(ImmutableKnownCopyNumber.builder()
                    .from(copyNumber)
                    .geneRole(GeneRole.UNKNOWN)
                    .proteinEffect(ProteinEffect.UNKNOWN)
                    .addSources(source)
                    .build());
        }

        return CopyNumberConsolidation.consolidate(copyNumbers);
    }

    @NotNull
    private static Set<KnownFusion> convertToKnownFusions(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        Set<KnownFusion> fusions = Sets.newHashSet();
        Knowledgebase source = ViccSource.toKnowledgebase(entry.source());
        for (FusionPair fusion : extraction.fusionsPerFeature().values()) {
            fusions.add(ImmutableKnownFusion.builder().from(fusion).proteinEffect(ProteinEffect.UNKNOWN).addSources(source).build());
        }

        return FusionConsolidation.consolidate(fusions);
    }

    private static void addActionability(@NotNull ImmutableExtractionResult.Builder outputBuilder,
            @NotNull ViccExtractionResult extraction) {
        Set<ActionableHotspot> actionableHotspots = Sets.newHashSet();
        Set<ActionableRange> actionableCodons = Sets.newHashSet();
        Set<ActionableRange> actionableExons = Sets.newHashSet();
        Set<ActionableGene> actionableGenes = Sets.newHashSet();
        Set<ActionableFusion> actionableFusions = Sets.newHashSet();
        Set<ActionableCharacteristic> actionableCharacteristics = Sets.newHashSet();

        actionableHotspots.addAll(extractActionableHotspots(extraction, extraction.hotspotsPerFeature()));
        actionableCodons.addAll(extractActionableRange(extraction, extraction.codonsPerFeature()));
        actionableExons.addAll(extractActionableRange(extraction, extraction.exonsPerFeature()));
        actionableGenes.addAll(extractActionableAmpsDels(extraction, extraction.ampsDelsPerFeature()));
        actionableGenes.addAll(extractActionableGenes(extraction, extraction.geneLevelEventsPerFeature()));
        actionableFusions.addAll(extractActionableFusions(extraction, extraction.fusionsPerFeature()));
        actionableCharacteristics.addAll(extractActionableCharacteristics(extraction, extraction.characteristicsPerFeature()));

        outputBuilder.actionableHotspots(actionableHotspots);
        outputBuilder.actionableCodons(actionableCodons);
        outputBuilder.actionableExons(actionableExons);
        outputBuilder.actionableGenes(actionableGenes);
        outputBuilder.actionableFusions(actionableFusions);
        outputBuilder.actionableCharacteristics(actionableCharacteristics);
    }

    @NotNull
    private static Set<ActionableHotspot> extractActionableHotspots(@NotNull ViccExtractionResult extraction,
            @NotNull Map<Feature, List<VariantHotspot>> hotspotPerFeature) {
        Set<ActionableHotspot> actionableHotspots = Sets.newHashSet();
        for (Map.Entry<Feature, List<VariantHotspot>> entry : hotspotPerFeature.entrySet()) {
            List<VariantHotspot> hotspots = entry.getValue();
            if (hotspots != null) {
                for (ActionableEvidence evidence : extraction.actionableEvidence()) {
                    ActionableEvidence modified = withSourceEvent(evidence, extraction.eventInterpretationPerFeature().get(entry.getKey()));
                    actionableHotspots.addAll(ActionableEventFactory.toActionableHotspots(modified, hotspots));
                }
            }
        }
        return actionableHotspots;
    }

    @NotNull
    private static <T extends RangeAnnotation> Set<ActionableRange> extractActionableRange(@NotNull ViccExtractionResult extraction,
                                                                                            @NotNull Map<Feature, List<T>> rangesPerFeature) {
        Set<ActionableRange> actionableRanges = Sets.newHashSet();
        for (Map.Entry<Feature, List<T>> entry : rangesPerFeature.entrySet()) {
            List<T> ranges = entry.getValue();
            if (ranges != null) {
                for (ActionableEvidence evidence : extraction.actionableEvidence()) {
                    ActionableEvidence modified = withSourceEvent(evidence, extraction.eventInterpretationPerFeature().get(entry.getKey()));
                    actionableRanges.addAll(ActionableEventFactory.toActionableRanges(modified, ranges));
                }
            }
        }
        return actionableRanges;
    }

    @NotNull
    private static Set<ActionableGene> extractActionableAmpsDels(@NotNull ViccExtractionResult extraction,
            @NotNull Map<Feature, GeneAnnotation> copyNumbersPerFeature) {
        Set<ActionableGene> actionableGenes = Sets.newHashSet();
        for (Map.Entry<Feature, GeneAnnotation> entry : copyNumbersPerFeature.entrySet()) {
            GeneAnnotation copyNumber = entry.getValue();
            if (copyNumber != null) {
                for (ActionableEvidence evidence : extraction.actionableEvidence()) {
                    ActionableEvidence modified = withSourceEvent(evidence, extraction.eventInterpretationPerFeature().get(entry.getKey()));
                    actionableGenes.add(ActionableEventFactory.geneAnnotationToActionableGene(modified, copyNumber));
                }
            }
        }
        return actionableGenes;
    }

    @NotNull
    private static Set<ActionableGene> extractActionableGenes(@NotNull ViccExtractionResult extraction,
            @NotNull Map<Feature, GeneAnnotation> geneLevelEventsPerFeature) {
        Set<ActionableGene> actionableGenes = Sets.newHashSet();
        for (Map.Entry<Feature, GeneAnnotation> entry : geneLevelEventsPerFeature.entrySet()) {
            GeneAnnotation geneAnnotation = entry.getValue();
            if (geneAnnotation != null) {
                for (ActionableEvidence evidence : extraction.actionableEvidence()) {
                    ActionableEvidence modified = withSourceEvent(evidence, extraction.eventInterpretationPerFeature().get(entry.getKey()));
                    actionableGenes.add(ActionableEventFactory.geneAnnotationToActionableGene(modified, geneAnnotation));
                }
            }
        }
        return actionableGenes;
    }

    @NotNull
    private static Set<ActionableFusion> extractActionableFusions(@NotNull ViccExtractionResult extraction,
            @NotNull Map<Feature, FusionPair> fusionsPerFeature) {
        Set<ActionableFusion> actionableFusions = Sets.newHashSet();
        for (Map.Entry<Feature, FusionPair> entry : fusionsPerFeature.entrySet()) {
            FusionPair fusionPair = entry.getValue();
            if (fusionPair != null) {
                for (ActionableEvidence evidence : extraction.actionableEvidence()) {
                    ActionableEvidence modified = withSourceEvent(evidence, extraction.eventInterpretationPerFeature().get(entry.getKey()));
                    actionableFusions.add(ActionableEventFactory.toActionableFusion(modified, fusionPair));
                }
            }
        }
        return actionableFusions;
    }

    @NotNull
    private static Set<ActionableCharacteristic> extractActionableCharacteristics(@NotNull ViccExtractionResult extraction,
            @NotNull Map<Feature, TumorCharacteristic> characteristicPerFeature) {
        Set<ActionableCharacteristic> actionableCharacteristics = Sets.newHashSet();
        for (Map.Entry<Feature, TumorCharacteristic> entry : characteristicPerFeature.entrySet()) {
            TumorCharacteristic characteristic = entry.getValue();
            if (characteristic != null) {
                for (ActionableEvidence evidence : extraction.actionableEvidence()) {
                    ActionableEvidence modified = withSourceEvent(evidence, extraction.eventInterpretationPerFeature().get(entry.getKey()));
                    actionableCharacteristics.add(ActionableEventFactory.toActionableCharacteristic(modified, characteristic));
                }
            }
        }
        return actionableCharacteristics;
    }

    @NotNull
    private static ActionableEvidence withSourceEvent(@NotNull ActionableEvidence evidence,
            @NotNull EventInterpretation eventInterpretation) {
        return ImmutableActionableEvidence.builder().from(evidence).sourceEvent(eventInterpretation.sourceEvent()).build();
    }
}