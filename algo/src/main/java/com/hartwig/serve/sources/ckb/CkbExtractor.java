package com.hartwig.serve.sources.ckb;

import static com.hartwig.serve.sources.ckb.CkbVariantAnnotator.resolveGeneRole;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.serve.ckb.classification.CkbConstants;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
import com.hartwig.serve.ckb.classification.CkbProteinAnnotationExtractor;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.ActionableTrial;
import com.hartwig.serve.datamodel.EfficacyEvidence;
import com.hartwig.serve.datamodel.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.KnownEvents;
import com.hartwig.serve.datamodel.MolecularCriterium;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.FusionPair;
import com.hartwig.serve.datamodel.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.gene.ImmutableKnownGene;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.range.RangeAnnotation;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ExtractionFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableEventExtractorOutput;
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
import com.hartwig.serve.extraction.immuno.ImmunoHLA;
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value;
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
        int variantCount = entry.variants().size();
        // TODO this is where we assume only 1 variant per entry
        Variant variant = entry.variants().get(0);
        String event = variantCount > 1 ? concat(entry.variants()) : CkbEventAndGeneExtractor.extractEvent(variant);
        String gene = variantCount > 1 ? "Multiple" : CkbEventAndGeneExtractor.extractGene(variant);

        if (entry.type() == EventType.UNKNOWN) {
            LOGGER.warn("No event type known for '{}' on '{}'", event, gene);
            return null;
        } else {
            EventExtractorOutput extractionOutput = curateCodons(eventExtractor.extract(gene, null, entry.type(), event));
            String sourceEvent = gene.equals(CkbConstants.NO_GENE) ? event : gene + " " + event;

            EventInterpretation interpretation = ImmutableEventInterpretation.builder()
                    .source(Knowledgebase.CKB)
                    .sourceEvent(sourceEvent)
                    .interpretedGene(gene)
                    .interpretedEvent(event)
                    .interpretedEventType(entry.type())
                    .build();

            MolecularCriterium molecularCriterium = createMolecularCriterium(extractionOutput, variant, event, entry);

            Set<EfficacyEvidence> efficacyEvidences = efficacyEvidenceFactory.create(entry, molecularCriterium, sourceEvent, gene);
            Set<ActionableTrial> actionableTrials = actionableTrialFactory.create(entry, molecularCriterium, sourceEvent, gene);

            return ImmutableExtractionResult.builder()
                    .refGenomeVersion(Knowledgebase.CKB.refGenomeVersion())
                    .eventInterpretations(Set.of(interpretation))
                    .knownEvents(generateKnownEvents(extractionOutput, efficacyEvidences.isEmpty(), variant, event, gene))
                    .evidences(efficacyEvidences)
                    .trials(actionableTrials)
                    .build();
        }
    }

    @NotNull
    private static String concat(@NotNull List<Variant> variants) {
        return variants.stream().map(Variant::variant).collect(Collectors.joining(VARIANT_DELIMITER));
    }

    @NotNull
    private KnownEvents generateKnownEvents(@NotNull EventExtractorOutput extractorOutput, boolean efficacyEvidencesIsEmpty,
            @NotNull Variant variant, @NotNull String event, @NotNull String gene) {
        return ImmutableKnownEvents.builder()
                .hotspots(convertToKnownHotspots(extractorOutput.hotspots(), event, variant))
                .codons(convertToKnownCodons(efficacyEvidencesIsEmpty ? Collections.emptyList() : extractorOutput.codons(), variant))
                .exons(convertToKnownExons(extractorOutput.exons(), variant))
                .genes(extractorOutput.fusionPair() == null ? convertToKnownGenes(gene, variant) : Collections.emptySet())
                .copyNumbers(convertToKnownCopyNumbers(extractorOutput.copyNumber(), variant))
                .fusions(convertToKnownFusions(extractorOutput.fusionPair(), variant))
                .build();
    }

    @NotNull
    private MolecularCriterium createMolecularCriterium(@NotNull EventExtractorOutput extractionOutput, @NotNull Variant variant,
            @NotNull String event, @NotNull CkbEntry entry) {
        Set<String> sourceUrls =
                Set.of("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=" + entry.profileId());
        ActionableEvidence actionableEvidence = toActionableEvidence(event, variant, sourceUrls);

        return ImmutableMolecularCriterium.builder()
                .hotspots(extractActionableHotspots(extractionOutput.hotspots(), actionableEvidence))
                .characteristics(extractActionableCharacteristic(extractionOutput.characteristic(), actionableEvidence))
                .exons(extractActionableRanges(extractionOutput.exons(), actionableEvidence))
                .codons(extractActionableRanges(extractionOutput.codons(), actionableEvidence))
                .genes(Stream.of(extractionOutput.geneLevel(), extractionOutput.copyNumber())
                        .filter(Objects::nonNull)
                        .map(annotation -> extractActionableGenes(annotation, actionableEvidence))
                        .collect(Collectors.toSet()))
                .fusions(extractActionableFusions(extractionOutput.fusionPair(), actionableEvidence))
                .hla(extractActionableHLA(extractionOutput.hla(), actionableEvidence))
                .build();
    }

    @VisibleForTesting
    @NotNull
    static EventExtractorOutput curateCodons(@NotNull EventExtractorOutput extractorOutput) {
        List<CodonAnnotation> codonAnnotations = extractorOutput.codons();
        if (codonAnnotations == null) {
            return extractorOutput;
        }
        List<CodonAnnotation> codons = codonAnnotations.stream().map(codon -> {
            if (codon.gene().equals("BRAF") && codon.inputCodonRank() == 600) {
                return ImmutableCodonAnnotation.copyOf(codon)
                        .withInputTranscript("ENST00000646891")
                        .withStart(140753335)
                        .withEnd(140753337);
            }
            return codon;
        }).collect(Collectors.toList());

        return ImmutableEventExtractorOutput.copyOf(extractorOutput).withCodons(codons);
    }

    @NotNull
    private <T, U> Set<U> convertToKnownSet(@Nullable List<T> rawList, @NotNull Function<T, U> convert,
            @NotNull Function<Set<U>, Set<U>> consolidate, @NotNull BiFunction<U, Variant, U> annotate, @NotNull Variant variant) {
        if (rawList == null) {
            return Collections.emptySet();
        }
        Set<U> converted = rawList.stream().map(convert).collect(Collectors.toSet());
        return consolidate.apply(converted).stream().map(e -> annotate.apply(e, variant)).collect(Collectors.toSet());
    }

    @NotNull
    private Set<KnownHotspot> convertToKnownHotspots(@Nullable List<VariantHotspot> hotspots, @NotNull String event,
            @NotNull Variant variant) {
        CkbProteinAnnotationExtractor proteinExtractor = new CkbProteinAnnotationExtractor();
        Function<VariantHotspot, KnownHotspot> convert = hotspot -> ImmutableKnownHotspot.builder()
                .from(hotspot)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .addSources(Knowledgebase.CKB)
                .inputProteinAnnotation(proteinExtractor.apply(event))
                .build();

        return convertToKnownSet(hotspots, convert, HotspotConsolidation::consolidate, CkbVariantAnnotator::annotateHotspot, variant);
    }

    @NotNull
    private Set<KnownCodon> convertToKnownCodons(@Nullable List<CodonAnnotation> codonAnnotations, @NotNull Variant variant) {
        Function<CodonAnnotation, KnownCodon> convert = codonAnnotation -> ImmutableKnownCodon.builder()
                .from(codonAnnotation)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .inputTranscript(codonAnnotation.inputTranscript())
                .inputCodonRank(codonAnnotation.inputCodonRank())
                .addSources(Knowledgebase.CKB)
                .build();

        return convertToKnownSet(codonAnnotations, convert, CodonConsolidation::consolidate, CkbVariantAnnotator::annotateCodon, variant);
    }

    @NotNull
    private Set<KnownExon> convertToKnownExons(@Nullable List<ExonAnnotation> exonAnnotations, @NotNull Variant variant) {
        Function<ExonAnnotation, KnownExon> convert = exonAnnotation -> ImmutableKnownExon.builder()
                .from(exonAnnotation)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .inputTranscript(exonAnnotation.inputTranscript())
                .inputExonRank(exonAnnotation.inputExonRank())
                .addSources(Knowledgebase.CKB)
                .build();
        return convertToKnownSet(exonAnnotations, convert, ExonConsolidation::consolidate, CkbVariantAnnotator::annotateExon, variant);
    }

    @NotNull
    private Set<KnownGene> convertToKnownGenes(@NotNull String gene, @NotNull Variant variant) {
        if (!gene.equals(CkbConstants.NO_GENE)) {
            return Set.of(ImmutableKnownGene.builder().gene(gene).geneRole(resolveGeneRole(variant)).addSources(Knowledgebase.CKB).build());
        }

        return Collections.emptySet();
    }

    @NotNull
    private Set<KnownCopyNumber> convertToKnownCopyNumbers(@Nullable GeneAnnotation copyNumber, @NotNull Variant variant) {
        if (copyNumber == null) {
            return Collections.emptySet();
        }
        Function<GeneAnnotation, KnownCopyNumber> convert = cn -> ImmutableKnownCopyNumber.builder()
                .from(cn)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .addSources(Knowledgebase.CKB)
                .build();

        return convertToKnownSet(List.of(copyNumber),
                convert,
                CopyNumberConsolidation::consolidate,
                CkbVariantAnnotator::annotateCopyNumber,
                variant);
    }

    @NotNull
    private Set<KnownFusion> convertToKnownFusions(@Nullable FusionPair fusion, @NotNull Variant variant) {
        if (fusion == null) {
            return Collections.emptySet();
        }
        Function<FusionPair, KnownFusion> convert = fusionPair -> ImmutableKnownFusion.builder()
                .from(fusionPair)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .addSources(Knowledgebase.CKB)
                .build();

        return convertToKnownSet(List.of(fusion), convert, FusionConsolidation::consolidate, CkbVariantAnnotator::annotateFusion, variant);
    }

    @NotNull
    private static Set<ActionableHotspot> extractActionableHotspots(@Nullable List<VariantHotspot> hotspots,
            @NotNull ActionableEvidence actionableEvidence) {
        if (hotspots == null) {
            return Collections.emptySet();
        }
        return hotspots.stream()
                .map(hotspot -> ImmutableActionableHotspot.builder().from(hotspot).from(actionableEvidence).build())
                .collect(Collectors.toSet());
    }

    @NotNull
    private static Set<ActionableCharacteristic> extractActionableCharacteristic(@Nullable TumorCharacteristic characteristic,
            @NotNull ActionableEvidence actionableEvidence) {
        if (characteristic == null) {
            return Collections.emptySet();
        }
        return Set.of(ImmutableActionableCharacteristic.builder().from(characteristic).from(actionableEvidence).build());
    }

    @NotNull
    private static Set<ActionableRange> extractActionableRanges(@Nullable List<? extends RangeAnnotation> ranges,
            @NotNull ActionableEvidence actionableEvidence) {
        if (ranges == null) {
            return Collections.emptySet();
        }
        return ranges.stream()
                .map(range -> ImmutableActionableRange.builder().from(range).from(actionableEvidence).build())
                .collect(Collectors.toSet());
    }

    @NotNull
    private static Set<ActionableHLA> extractActionableHLA(@Nullable ImmunoHLA hla, @NotNull ActionableEvidence actionableEvidence) {
        if (hla == null) {
            return Collections.emptySet();
        }
        return Set.of(ImmutableActionableHLA.builder().from(hla).from(actionableEvidence).build());
    }

    @NotNull
    private static Set<ActionableFusion> extractActionableFusions(@Nullable FusionPair fusionPair,
            @NotNull ActionableEvidence actionableEvidence) {
        if (fusionPair == null) {
            return Collections.emptySet();
        }
        return Set.of(ImmutableActionableFusion.builder().from(fusionPair).from(actionableEvidence).build());
    }

    @NotNull
    public static ActionableGene extractActionableGenes(@NotNull GeneAnnotation geneAnnotation,
            @NotNull ActionableEvidence actionableEvidence) {
        return ImmutableActionableGene.builder().from(geneAnnotation).from(actionableEvidence).build();
    }

    @NotNull
    private static ActionableEvidence toActionableEvidence(@NotNull String event, @NotNull Variant variant,
            @NotNull Set<String> sourceUrls) {
        return ImmutableActionableEvidence.builder().sourceDate(variant.createDate()).sourceEvent(event).sourceUrls(sourceUrls).build();
    }

    @Value.Immutable
    @Value.Style(passAnnotations = { NotNull.class, Nullable.class })
    abstract static class ActionableEvidence implements ActionableEvent {
    }
}

