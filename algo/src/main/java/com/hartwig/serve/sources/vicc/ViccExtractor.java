package com.hartwig.serve.sources.vicc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ImmutableActionableEventImpl;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.ActionableEvent;
import com.hartwig.serve.datamodel.molecular.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.molecular.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.molecular.common.GeneRole;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.fusion.FusionPair;
import com.hartwig.serve.datamodel.molecular.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.molecular.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.molecular.fusion.KnownFusion;
import com.hartwig.serve.datamodel.molecular.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.molecular.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.molecular.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.molecular.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.molecular.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.molecular.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.molecular.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.molecular.range.KnownCodon;
import com.hartwig.serve.datamodel.molecular.range.KnownExon;
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
    private final ViccEfficacyEvidenceFactory efficacyEvidenceFactory;

    public ViccExtractor(@NotNull final EventExtractor eventExtractor,
            @NotNull final ViccEfficacyEvidenceFactory efficacyEvidenceFactory) {
        this.eventExtractor = eventExtractor;
        this.efficacyEvidenceFactory = efficacyEvidenceFactory;
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

        efficacyEvidenceFactory.evaluateCuration();
        ViccUtil.printExtractionResults(resultsPerEntry);
        return ExtractionFunctions.merge(extractions);
    }

    @NotNull
    private ViccExtractionResult extractEntry(@NotNull ViccEntry entry) {
        Map<Feature, EventInterpretation> eventInterpretationPerFeature = Maps.newHashMap();
        Map<Feature, List<VariantHotspot>> variantsPerFeature = Maps.newHashMap();
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
                        ImmutableEventInterpretation.builder().source(ViccEfficacyEvidenceFactory.fromViccSource(entry.source()))
                                .sourceEvent(sourceEvent)
                                .interpretedGene(gene)
                                .interpretedEvent(feature.name())
                                .interpretedEventType(feature.type())
                                .build());

                if (extractorOutput.hotspots() != null) {
                    variantsPerFeature.put(feature, extractorOutput.hotspots());
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

        ViccExtractionResult molecularExtraction = ImmutableViccExtractionResult.builder()
                .eventInterpretationPerFeature(eventInterpretationPerFeature)
                .variantsPerFeature(variantsPerFeature)
                .codonsPerFeature(codonsPerFeature)
                .exonsPerFeature(exonsPerFeature)
                .geneLevelEventsPerFeature(geneLevelEventsPerFeature)
                .ampsDelsPerFeature(ampsDelsPerFeature)
                .fusionsPerFeature(fusionsPerFeature)
                .characteristicsPerFeature(characteristicsPerFeature)
                .HLAPerFeature(hlaPerFeature)
                .build();

        Set<MolecularCriterium> molecularCriteria = toMolecularCriteria(molecularExtraction);

        return ImmutableViccExtractionResult.builder()
                .from(molecularExtraction)
                .efficacyEvidences(efficacyEvidenceFactory.toEfficacyEvidence(entry, molecularCriteria))
                .build();
    }

    @NotNull
    private static ExtractionResult toExtractionResult(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        // Assume all VICC knowledgebases are on the same ref genome version
        ImmutableExtractionResult.Builder outputBuilder = ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.VICC_CGI.refGenomeVersion())
                .addAllEventInterpretations(extraction.eventInterpretationPerFeature().values())
                .knownEvents(toKnownEvents(entry, extraction))
                .evidences(extraction.efficacyEvidences())
                .trials(null);

        return outputBuilder.build();
    }

    @NotNull
    private static KnownEvents toKnownEvents(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        return ImmutableKnownEvents.builder()
                .hotspots(convertToHotspots(entry, extraction))
                .codons(convertToCodons(entry, extraction))
                .exons(convertToExons(entry, extraction))
                .copyNumbers(convertToKnownCopyNumbers(entry, extraction))
                .fusions(convertToKnownFusions(entry, extraction))
                .build();
    }

    @NotNull
    private static Set<KnownHotspot> convertToHotspots(@NotNull ViccEntry entry, @NotNull ViccExtractionResult extraction) {
        ViccProteinAnnotationExtractor proteinExtractor = new ViccProteinAnnotationExtractor();
        Set<KnownHotspot> hotspots = Sets.newHashSet();
        Knowledgebase source = ViccSource.toKnowledgebase(entry.source());
        for (Map.Entry<Feature, List<VariantHotspot>> featureResult : extraction.variantsPerFeature().entrySet()) {
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

    @NotNull
    private static Set<MolecularCriterium> toMolecularCriteria(@NotNull ViccExtractionResult molecularExtraction) {
        Map<Feature, EventInterpretation> interpretations = molecularExtraction.eventInterpretationPerFeature();

        Set<MolecularCriterium> molecularCriteria = Sets.newHashSet();
        molecularCriteria.addAll(extractActionableHotspots(interpretations, molecularExtraction.variantsPerFeature()));
        molecularCriteria.addAll(extractActionableCodons(interpretations, molecularExtraction.codonsPerFeature()));
        molecularCriteria.addAll(extractActionableExons(interpretations, molecularExtraction.exonsPerFeature()));
        molecularCriteria.addAll(extractActionableGenes(interpretations, molecularExtraction.ampsDelsPerFeature()));
        molecularCriteria.addAll(extractActionableGenes(interpretations, molecularExtraction.geneLevelEventsPerFeature()));
        molecularCriteria.addAll(extractActionableFusions(interpretations, molecularExtraction.fusionsPerFeature()));
        molecularCriteria.addAll(extractActionableCharacteristics(interpretations, molecularExtraction.characteristicsPerFeature()));

        return molecularCriteria;
    }

    @NotNull
    private static Set<MolecularCriterium> extractActionableHotspots(@NotNull Map<Feature, EventInterpretation> interpretations,
            @NotNull Map<Feature, List<VariantHotspot>> hotspotPerFeature) {
        Set<MolecularCriterium> criteriaForHotspots = Sets.newHashSet();
        for (Map.Entry<Feature, List<VariantHotspot>> entry : hotspotPerFeature.entrySet()) {
            List<VariantHotspot> hotspots = entry.getValue();
            if (hotspots != null) {
                ActionableEvent event = toActionableEvent(interpretations.get(entry.getKey()));
                for (VariantHotspot hotspot : hotspots) {
                    criteriaForHotspots.add(ImmutableMolecularCriterium.builder()
                            .addHotspots(ImmutableActionableHotspot.builder().from(hotspot).from(event).build())
                            .build());
                }
            }
        }
        return criteriaForHotspots;
    }

    @NotNull
    private static Set<MolecularCriterium> extractActionableCodons(@NotNull Map<Feature, EventInterpretation> interpretations,
            @NotNull Map<Feature, List<CodonAnnotation>> codonsPerFeature) {
        Set<MolecularCriterium> criteriaForCodons = Sets.newHashSet();
        for (Map.Entry<Feature, List<CodonAnnotation>> entry : codonsPerFeature.entrySet()) {
            List<CodonAnnotation> codons = entry.getValue();
            if (codons != null) {
                ActionableEvent event = toActionableEvent(interpretations.get(entry.getKey()));
                for (CodonAnnotation codon : codons) {
                    criteriaForCodons.add(ImmutableMolecularCriterium.builder()
                            .addCodons(ImmutableActionableRange.builder().from(codon).from(event).build())
                            .build());
                }
            }
        }
        return criteriaForCodons;
    }

    @NotNull
    private static Set<MolecularCriterium> extractActionableExons(@NotNull Map<Feature, EventInterpretation> interpretations,
            @NotNull Map<Feature, List<ExonAnnotation>> rangesPerFeature) {
        Set<MolecularCriterium> criteriaForExons = Sets.newHashSet();
        for (Map.Entry<Feature, List<ExonAnnotation>> entry : rangesPerFeature.entrySet()) {
            List<ExonAnnotation> exons = entry.getValue();
            if (exons != null) {
                ActionableEvent event = toActionableEvent(interpretations.get(entry.getKey()));
                for (ExonAnnotation exon : exons) {
                    criteriaForExons.add(ImmutableMolecularCriterium.builder()
                            .addExons(ImmutableActionableRange.builder().from(exon).from(event).build())
                            .build());
                }
            }
        }
        return criteriaForExons;
    }

    @NotNull
    private static Set<MolecularCriterium> extractActionableGenes(@NotNull Map<Feature, EventInterpretation> interpretations,
            @NotNull Map<Feature, GeneAnnotation> genesPerFeature) {
        Set<MolecularCriterium> criteriaForGenes = Sets.newHashSet();
        for (Map.Entry<Feature, GeneAnnotation> entry : genesPerFeature.entrySet()) {
            GeneAnnotation annotation = entry.getValue();
            if (annotation != null) {
                ActionableEvent event = toActionableEvent(interpretations.get(entry.getKey()));
                criteriaForGenes.add(ImmutableMolecularCriterium.builder()
                        .addGenes(ImmutableActionableGene.builder().from(annotation).from(event).build())
                        .build());
            }
        }
        return criteriaForGenes;
    }

    @NotNull
    private static Set<MolecularCriterium> extractActionableFusions(@NotNull Map<Feature, EventInterpretation> interpretations,
            @NotNull Map<Feature, FusionPair> fusionsPerFeature) {
        Set<MolecularCriterium> criteriaForFusions = Sets.newHashSet();
        for (Map.Entry<Feature, FusionPair> entry : fusionsPerFeature.entrySet()) {
            FusionPair fusionPair = entry.getValue();
            if (fusionPair != null) {
                ActionableEvent event = toActionableEvent(interpretations.get(entry.getKey()));
                criteriaForFusions.add(ImmutableMolecularCriterium.builder()
                        .addFusions(ImmutableActionableFusion.builder().from(fusionPair).from(event).build())
                        .build());
            }
        }
        return criteriaForFusions;
    }

    @NotNull
    private static Set<MolecularCriterium> extractActionableCharacteristics(
            @NotNull Map<Feature, EventInterpretation> interpretationPerFeature,
            @NotNull Map<Feature, TumorCharacteristic> characteristicPerFeature) {
        Set<MolecularCriterium> criteriaForCharacteristics = Sets.newHashSet();
        for (Map.Entry<Feature, TumorCharacteristic> entry : characteristicPerFeature.entrySet()) {
            TumorCharacteristic characteristic = entry.getValue();
            if (characteristic != null) {
                ActionableEvent event = toActionableEvent(interpretationPerFeature.get(entry.getKey()));
                criteriaForCharacteristics.add(ImmutableMolecularCriterium.builder()
                        .addCharacteristics(ImmutableActionableCharacteristic.builder().from(characteristic).from(event).build())
                        .build());
            }
        }
        return criteriaForCharacteristics;
    }

    @NotNull
    private static ActionableEvent toActionableEvent(@NotNull EventInterpretation interpretation) {
        return ImmutableActionableEventImpl.builder()
                .sourceDate(LocalDate.EPOCH)
                .sourceEvent(interpretation.sourceEvent())
                .sourceUrls(Sets.newHashSet())
                .build();
    }
}