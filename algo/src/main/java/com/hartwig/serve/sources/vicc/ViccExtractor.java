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
import com.hartwig.serve.datamodel.common.GeneAlteration;
import com.hartwig.serve.datamodel.common.Variant;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.ImmutableKnownFusionPair;
import com.hartwig.serve.datamodel.fusion.KnownFusionPair;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.CodonAnnotation;
import com.hartwig.serve.datamodel.range.ExonAnnotation;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.range.RangeAnnotation;
import com.hartwig.serve.extraction.ActionableEventFactory;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ExtractionFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.codon.CodonFunctions;
import com.hartwig.serve.extraction.copynumber.CopyNumberFunctions;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.events.ImmutableEventInterpretation;
import com.hartwig.serve.extraction.exon.ExonFunctions;
import com.hartwig.serve.extraction.fusion.FusionFunctions;
import com.hartwig.serve.extraction.hotspot.HotspotFunctions;
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
        Map<Feature, GeneAnnotation> geneAnnotationsPerFeature = Maps.newHashMap();
        Map<Feature, KnownCopyNumber> ampsDelsPerFeature = Maps.newHashMap();
        Map<Feature, KnownFusionPair> fusionsPerFeature = Maps.newHashMap();
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

                if (extractorOutput.geneAnnotation() != null) {
                    geneAnnotationsPerFeature.put(feature, extractorOutput.geneAnnotation());
                }

                if (extractorOutput.knownCopyNumber() != null) {
                    ampsDelsPerFeature.put(feature, extractorOutput.knownCopyNumber());
                }

                if (extractorOutput.knownFusionPair() != null) {
                    fusionsPerFeature.put(feature, extractorOutput.knownFusionPair());
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
                .geneAnnotationsPerFeature(geneAnnotationsPerFeature)
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
                .knownCopyNumbers(convertToKnownAmpsDels(entry, extraction))
                .knownFusionPairs(convertToKnownFusions(entry, extraction));

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
                        .from((Variant) hotspot)
                        .from((GeneAlteration) hotspot)
                        .addSources(source)
                        .gene(feature.geneSymbol())
                        .transcript(entry.transcriptId())
                        .proteinAnnotation(proteinExtractor.apply(feature.name()))
                        .build());
            }
        }

        return HotspotFunctions.consolidate(hotspots);
    }

    @NotNull
    private static Set<KnownCodon> convertToCodons(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        Set<KnownCodon> codons = Sets.newHashSet();

        Knowledgebase source = ViccSource.toKnowledgebase(entry.source());
        for (List<CodonAnnotation> annotations : extraction.codonsPerFeature().values()) {
            for (CodonAnnotation annotation : annotations) {
                codons.add(ImmutableKnownCodon.builder().annotation(annotation).addSources(source).build());
            }
        }

        return CodonFunctions.consolidate(codons);
    }

    @NotNull
    private static Set<KnownExon> convertToExons(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        Set<KnownExon> exons = Sets.newHashSet();

        Knowledgebase source = ViccSource.toKnowledgebase(entry.source());
        for (List<ExonAnnotation> annotations : extraction.exonsPerFeature().values()) {
            for (ExonAnnotation annotation : annotations) {
                exons.add(ImmutableKnownExon.builder().annotation(annotation).addSources(source).build());
            }
        }

        return ExonFunctions.consolidate(exons);
    }

    @NotNull
    private static Set<KnownCopyNumber> convertToKnownAmpsDels(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        Set<KnownCopyNumber> copyNumbers = Sets.newHashSet();
        Knowledgebase source = ViccSource.toKnowledgebase(entry.source());
        for (KnownCopyNumber copyNumber : extraction.ampsDelsPerFeature().values()) {
            copyNumbers.add(ImmutableKnownCopyNumber.builder().from(copyNumber).addSources(source).build());
        }

        return CopyNumberFunctions.consolidate(copyNumbers);
    }

    @NotNull
    private static Set<KnownFusionPair> convertToKnownFusions(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        Set<KnownFusionPair> fusions = Sets.newHashSet();
        Knowledgebase source = ViccSource.toKnowledgebase(entry.source());
        for (KnownFusionPair fusionPair : extraction.fusionsPerFeature().values()) {
            fusions.add(ImmutableKnownFusionPair.builder().from(fusionPair).addSources(source).build());
        }

        return FusionFunctions.consolidate(fusions);
    }

    private static void addActionability(@NotNull ImmutableExtractionResult.Builder outputBuilder,
            @NotNull ViccExtractionResult extraction) {
        Set<ActionableHotspot> actionableHotspots = Sets.newHashSet();
        Set<ActionableRange> actionableRanges = Sets.newHashSet();
        Set<ActionableGene> actionableGenes = Sets.newHashSet();
        Set<ActionableFusion> actionableFusions = Sets.newHashSet();
        Set<ActionableCharacteristic> actionableCharacteristics = Sets.newHashSet();

        actionableHotspots.addAll(extractActionableHotspots(extraction, extraction.hotspotsPerFeature()));
        actionableRanges.addAll(extractActionableRanges(extraction, extraction.codonsPerFeature()));
        actionableRanges.addAll(extractActionableRanges(extraction, extraction.exonsPerFeature()));
        actionableGenes.addAll(extractActionableAmpsDels(extraction, extraction.ampsDelsPerFeature()));
        actionableGenes.addAll(extractActionableGenes(extraction, extraction.geneAnnotationsPerFeature()));
        actionableFusions.addAll(extractActionableFusions(extraction, extraction.fusionsPerFeature()));
        actionableCharacteristics.addAll(extractActionableCharacteristics(extraction, extraction.characteristicsPerFeature()));

        outputBuilder.actionableHotspots(actionableHotspots);
        outputBuilder.actionableRanges(actionableRanges);
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
    private static <T extends RangeAnnotation> Set<ActionableRange> extractActionableRanges(@NotNull ViccExtractionResult extraction,
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
            @NotNull Map<Feature, KnownCopyNumber> copyNumbersPerFeature) {
        Set<ActionableGene> actionableGenes = Sets.newHashSet();
        for (Map.Entry<Feature, KnownCopyNumber> entry : copyNumbersPerFeature.entrySet()) {
            KnownCopyNumber knownCopyNumber = entry.getValue();
            if (knownCopyNumber != null) {
                for (ActionableEvidence evidence : extraction.actionableEvidence()) {
                    ActionableEvidence modified = withSourceEvent(evidence, extraction.eventInterpretationPerFeature().get(entry.getKey()));
                    actionableGenes.add(ActionableEventFactory.copyNumberToActionableGene(modified, knownCopyNumber));
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
            @NotNull Map<Feature, KnownFusionPair> knownFusionPerFeature) {
        Set<ActionableFusion> actionableFusions = Sets.newHashSet();
        for (Map.Entry<Feature, KnownFusionPair> entry : knownFusionPerFeature.entrySet()) {
            KnownFusionPair fusionPair = entry.getValue();
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