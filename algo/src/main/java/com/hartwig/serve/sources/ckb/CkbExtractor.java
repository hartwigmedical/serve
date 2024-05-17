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
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.fusion.FusionPair;
import com.hartwig.serve.datamodel.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.gene.ImmutableKnownGene;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.datamodel.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.extraction.ActionableEventFactory;
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
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbExtractor {

    private static final Logger LOGGER = LogManager.getLogger(CkbExtractor.class);
    private static final String VARIANT_DELIMITER = ",";

    @NotNull
    private final Knowledgebase source;
    @NotNull
    private final EventExtractor eventExtractor;
    @NotNull
    private final ActionableEntryFactory actionableEntryFactory;
    private final boolean generateKnownEvents;

    CkbExtractor(@NotNull Knowledgebase source, @NotNull final EventExtractor eventExtractor,
            @NotNull ActionableEntryFactory actionableEntryFactory, boolean generateKnownEvents) {
        this.source = source;
        this.eventExtractor = eventExtractor;
        this.actionableEntryFactory = actionableEntryFactory;
        this.generateKnownEvents = generateKnownEvents;
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
        Variant variant = entry.variants().get(0);
        String event = variantCount > 1 ? concat(entry.variants()) : CkbEventAndGeneExtractor.extractEvent(variant);
        String gene = variantCount > 1 ? "Multiple" : CkbEventAndGeneExtractor.extractGene(variant);

        if (entry.type() == EventType.UNKNOWN) {
            LOGGER.warn("No event type known for '{}' on '{}'", event, gene);
            return null;
        } else {
            EventExtractorOutput extractionOutput = curateCodons(eventExtractor.extract(gene, null, entry.type(), event));
            String sourceEvent = gene.equals(CkbConstants.NO_GENE) ? event : gene + " " + event;

            Set<ActionableEntry> actionableEntries = actionableEntryFactory.create(entry, sourceEvent, gene);

            EventInterpretation interpretation = ImmutableEventInterpretation.builder()
                    .source(source)
                    .sourceEvent(sourceEvent)
                    .interpretedGene(gene)
                    .interpretedEvent(event)
                    .interpretedEventType(entry.type())
                    .build();

            ImmutableExtractionResult.Builder extractionResultBuilder = actionableEntries.stream()
                    .map(actionableEntry -> actionableEntryToResultBuilder(extractionOutput, actionableEntry))
                    .reduce(ImmutableExtractionResult.builder(), CkbExtractor::mergeResultBuilders);

            if (generateKnownEvents) {
                extractionResultBuilder.knownHotspots(convertToKnownHotspots(extractionOutput.hotspots(), event, variant))
                        .knownCodons(convertToKnownCodons(actionableEntries.isEmpty() ? Collections.emptyList() : extractionOutput.codons(),
                                variant))
                        .knownExons(convertToKnownExons(extractionOutput.exons(), variant))
                        .knownGenes(extractionOutput.fusionPair() == null ? convertToKnownGenes(gene, variant) : Collections.emptySet())
                        .knownCopyNumbers(convertToKnownCopyNumbers(extractionOutput.copyNumber(), variant))
                        .knownFusions(convertToKnownFusions(extractionOutput.fusionPair(), variant));
            }

            return extractionResultBuilder.refGenomeVersion(source.refGenomeVersion()).addEventInterpretations(interpretation).build();
        }
    }

    @NotNull
    private static String concat(@NotNull List<Variant> variants) {
        return variants.stream().map(Variant::variant).collect(Collectors.joining(VARIANT_DELIMITER));
    }

    @NotNull
    private static <T, U> Set<U> extractNonNullToSet(@Nullable T raw, @NotNull ActionableEntry event,
            @NotNull BiFunction<ActionableEntry, T, U> extract) {
        return (raw == null) ? Collections.emptySet() : Set.of(extract.apply(event, raw));
    }

    @NotNull
    private static ImmutableExtractionResult.Builder mergeResultBuilders(@NotNull ImmutableExtractionResult.Builder a,
            @NotNull ImmutableExtractionResult.Builder b) {
        ExtractionResult built = b.build();
        a.addAllActionableHotspots(built.actionableHotspots());
        a.addAllActionableCodons(built.actionableCodons());
        a.addAllActionableExons(built.actionableExons());
        a.addAllActionableGenes(built.actionableGenes());
        a.addAllActionableFusions(built.actionableFusions());
        a.addAllActionableCharacteristics(built.actionableCharacteristics());
        a.addAllActionableHLA(built.actionableHLA());
        return a;
    }

    @NotNull
    private ImmutableExtractionResult.Builder actionableEntryToResultBuilder(@NotNull EventExtractorOutput output,
            @NotNull ActionableEntry entry) {
        return ImmutableExtractionResult.builder()
                .refGenomeVersion(source.refGenomeVersion())
                .actionableHotspots(ActionableEventFactory.toActionableHotspots(entry, output.hotspots()))
                .actionableCodons(ActionableEventFactory.toActionableRanges(entry, output.codons()))
                .actionableExons(ActionableEventFactory.toActionableRanges(entry, output.exons()))
                .actionableGenes(Stream.of(output.geneLevel(), output.copyNumber())
                        .filter(Objects::nonNull)
                        .map(annotation -> ActionableEventFactory.geneAnnotationToActionableGene(entry, annotation))
                        .collect(Collectors.toSet()))
                .actionableFusions(extractNonNullToSet(output.fusionPair(), entry, ActionableEventFactory::toActionableFusion))
                .actionableCharacteristics(extractNonNullToSet(output.characteristic(),
                        entry,
                        ActionableEventFactory::toActionableCharacteristic))
                .actionableHLA(extractNonNullToSet(output.hla(), entry, ActionableEventFactory::toActionableHLa));
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
                .addSources(source)
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
                .addSources(source)
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
                .addSources(source)
                .build();
        return convertToKnownSet(exonAnnotations, convert, ExonConsolidation::consolidate, CkbVariantAnnotator::annotateExon, variant);
    }

    @NotNull
    private Set<KnownGene> convertToKnownGenes(@NotNull String gene, @NotNull Variant variant) {
        if (!gene.equals(CkbConstants.NO_GENE)) {
            return Set.of(ImmutableKnownGene.builder().gene(gene).geneRole(resolveGeneRole(variant)).addSources(source).build());
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
                .addSources(source)
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
                .addSources(source)
                .build();

        return convertToKnownSet(List.of(fusion), convert, FusionConsolidation::consolidate, CkbVariantAnnotator::annotateFusion, variant);
    }
}

