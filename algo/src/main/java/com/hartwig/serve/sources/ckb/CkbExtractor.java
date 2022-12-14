package com.hartwig.serve.sources.ckb;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.classification.CkbConstants;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
import com.hartwig.serve.ckb.classification.CkbProteinAnnotationExtractor;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
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
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.extraction.ActionableEventFactory;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ExtractionFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.CodonConsolidation;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.extraction.copynumber.CopyNumberConsolidation;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.events.ImmutableEventInterpretation;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.exon.ExonConsolidation;
import com.hartwig.serve.extraction.fusion.FusionConsolidation;
import com.hartwig.serve.extraction.hotspot.HotspotConsolidation;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbExtractor {

    private static final Logger LOGGER = LogManager.getLogger(CkbExtractor.class);
    private static final String DELIMITER = ",";

    @NotNull
    private final EventExtractor eventExtractor;
    @NotNull
    private final TreatmentApproachCurator treatmentApproachCurator;

    public CkbExtractor(@NotNull final EventExtractor eventExtractor, @NotNull final TreatmentApproachCurator treatmentApproachCurator) {
        this.eventExtractor = eventExtractor;
        this.treatmentApproachCurator = treatmentApproachCurator;
    }

    @NotNull
    public ExtractionResult extract(@NotNull List<CkbEntry> entries) {
        List<ExtractionResult> extractions = Lists.newArrayList();

        ProgressTracker tracker = new ProgressTracker("CKB", entries.size());
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

                Set<ActionableEntry> actionableEntries =
                        ActionableEntryFactory.toActionableEntries(entry, sourceEvent, treatmentApproachCurator, gene, entry.type());

                EventInterpretation interpretation = ImmutableEventInterpretation.builder()
                        .source(Knowledgebase.CKB)
                        .sourceEvent(sourceEvent)
                        .interpretedGene(gene)
                        .interpretedEvent(event)
                        .interpretedEventType(entry.type())
                        .build();

                ExtractionResult extraction = toExtractionResult(event, null, extractionOutput, actionableEntries, interpretation);
                extractions.add(CkbVariantAnnotator.annotate(extraction, variant));
            }

            tracker.update();
        }

        treatmentApproachCurator.reportUnusedCuratedEntries();

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
    private static ExtractionResult toExtractionResult(@NotNull String variant, @Nullable String transcript,
            @NotNull EventExtractorOutput output, @NotNull Set<ActionableEntry> actionableEntries,
            @NotNull EventInterpretation interpretation) {
        Set<ActionableHotspot> actionableHotspots = Sets.newHashSet();
        Set<ActionableRange> actionableRanges = Sets.newHashSet();
        Set<ActionableGene> actionableGenes = Sets.newHashSet();
        Set<ActionableFusion> actionableFusions = Sets.newHashSet();
        Set<ActionableCharacteristic> actionableCharacteristics = Sets.newHashSet();
        Set<ActionableHLA> actionableHLA = Sets.newHashSet();

        List<CodonAnnotation> codons = Lists.newArrayList();

        for (ActionableEvent event : actionableEntries) {
            codons = curateCodons(output.codons());

            actionableHotspots.addAll(ActionableEventFactory.toActionableHotspots(event, output.hotspots()));
            actionableRanges.addAll(ActionableEventFactory.toActionableRanges(event, codons));
            actionableRanges.addAll(ActionableEventFactory.toActionableRanges(event, output.exons()));

            if (output.geneLevel() != null) {
                actionableGenes.add(ActionableEventFactory.geneAnnotationToActionableGene(event, output.geneLevel()));
            }

            if (output.copyNumber() != null) {
                actionableGenes.add(ActionableEventFactory.geneAnnotationToActionableGene(event, output.copyNumber()));
            }

            if (output.fusionPair() != null) {
                actionableFusions.add(ActionableEventFactory.toActionableFusion(event, output.fusionPair()));
            }

            if (output.characteristic() != null) {
                actionableCharacteristics.add(ActionableEventFactory.toActionableCharacteristic(event, output.characteristic()));
            }

            if (output.hla() != null) {
                actionableHLA.add(ActionableEventFactory.toActionableHLa(event, output.hla()));
            }
        }

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.CKB.refGenomeVersion())
                .addEventInterpretations(interpretation)
                .knownHotspots(convertToKnownHotspots(output.hotspots(), variant, transcript))
                .knownCodons(convertToKnownCodons(codons))
                .knownExons(convertToKnownExons(output.exons()))
                .knownCopyNumbers(convertToKnownAmpsDels(output.copyNumber()))
                .knownFusions(convertToKnownFusions(output.fusionPair()))
                .actionableHotspots(actionableHotspots)
                .actionableRanges(actionableRanges)
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

    @NotNull
    private static Set<KnownHotspot> convertToKnownHotspots(@Nullable List<VariantHotspot> hotspots, @NotNull String variant,
            @Nullable String transcript) {
        Set<KnownHotspot> knownHotspots = Sets.newHashSet();

        if (hotspots != null) {
            CkbProteinAnnotationExtractor proteinExtractor = new CkbProteinAnnotationExtractor();
            for (VariantHotspot hotspot : hotspots) {
                knownHotspots.add(ImmutableKnownHotspot.builder()
                        .from(hotspot)
                        .geneRole(GeneRole.UNKNOWN)
                        .proteinEffect(ProteinEffect.UNKNOWN)
                        .addSources(Knowledgebase.CKB)
                        .inputTranscript(transcript)
                        .inputProteinAnnotation(proteinExtractor.apply(variant))
                        .build());
            }
        }

        return HotspotConsolidation.consolidate(knownHotspots);
    }

    @NotNull
    private static Set<KnownCodon> convertToKnownCodons(@Nullable List<CodonAnnotation> codonAnnotations) {
        Set<KnownCodon> codons = Sets.newHashSet();

        if (codonAnnotations != null) {
            for (CodonAnnotation codonAnnotation : codonAnnotations) {
                codons.add(ImmutableKnownCodon.builder()
                        .from(codonAnnotation)
                        .geneRole(GeneRole.UNKNOWN)
                        .proteinEffect(ProteinEffect.UNKNOWN)
                        .inputTranscript(codonAnnotation.inputTranscript())
                        .inputCodonRank(codonAnnotation.inputCodonRank())
                        .addSources(Knowledgebase.CKB)
                        .build());
            }
        }

        return CodonConsolidation.consolidate(codons);
    }

    @NotNull
    private static Set<KnownExon> convertToKnownExons(@Nullable List<ExonAnnotation> exonAnnotations) {
        Set<KnownExon> exons = Sets.newHashSet();

        if (exonAnnotations != null) {
            for (ExonAnnotation exonAnnotation : exonAnnotations) {
                exons.add(ImmutableKnownExon.builder()
                        .from(exonAnnotation)
                        .geneRole(GeneRole.UNKNOWN)
                        .proteinEffect(ProteinEffect.UNKNOWN)
                        .inputTranscript(exonAnnotation.inputTranscript())
                        .inputExonRank(exonAnnotation.inputExonRank())
                        .addSources(Knowledgebase.CKB)
                        .build());
            }
        }

        return ExonConsolidation.consolidate(exons);
    }

    @NotNull
    private static Set<KnownCopyNumber> convertToKnownAmpsDels(@Nullable GeneAnnotation copyNumber) {
        Set<KnownCopyNumber> copyNumbers = Sets.newHashSet();

        if (copyNumber != null) {
            copyNumbers.add(ImmutableKnownCopyNumber.builder()
                    .from(copyNumber)
                    .geneRole(GeneRole.UNKNOWN)
                    .proteinEffect(ProteinEffect.UNKNOWN)
                    .addSources(Knowledgebase.CKB)
                    .build());
        }

        return CopyNumberConsolidation.consolidate(copyNumbers);
    }

    @NotNull
    private static Set<KnownFusion> convertToKnownFusions(@Nullable FusionPair fusion) {
        Set<KnownFusion> fusions = Sets.newHashSet();

        if (fusion != null) {
            fusions.add(ImmutableKnownFusion.builder()
                    .from(fusion)
                    .proteinEffect(ProteinEffect.UNKNOWN)
                    .addSources(Knowledgebase.CKB)
                    .build());
        }

        return FusionConsolidation.consolidate(fusions);
    }
}