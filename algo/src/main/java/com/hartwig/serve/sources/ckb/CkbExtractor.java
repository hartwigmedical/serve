package com.hartwig.serve.sources.ckb;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        List<ExtractionResult> extractions = entries.parallelStream().map(entry -> getExtractionResult(entry, tracker))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        return ExtractionFunctions.merge(extractions);
    }

    @NotNull
    private Optional<ExtractionResult> getExtractionResult(@NotNull CkbEntry entry, @NotNull ProgressTracker tracker) {
        if (entry.variants().isEmpty()) {
            throw new IllegalStateException("A CKB entry without variants has been provided for extraction: " + entry);
        }
        int variantCount = entry.variants().size();
        Variant variant = entry.variants().get(0);
        String event = variantCount > 1 ? concat(entry.variants()) : CkbEventAndGeneExtractor.extractEvent(variant);
        String gene = variantCount > 1 ? "Multiple" : CkbEventAndGeneExtractor.extractGene(variant);
        
        Optional<ExtractionResult> result;
        if (entry.type() == EventType.UNKNOWN) {
            LOGGER.warn("No event type known for '{}' on '{}'", event, gene);
            result = Optional.empty();
        } else {
            ExtractionResult extraction = extractAndInterpretEvent(entry, gene, event);
            result = Optional.of(generateKnownEvents ? CkbVariantAnnotator.annotate(extraction, variant) : extraction);
        }
        tracker.update();
        return result;
    }

    @NotNull
    private ExtractionResult extractAndInterpretEvent(@NotNull CkbEntry entry, @NotNull String gene, @NotNull String event) {
        EventExtractorOutput extractionOutput = eventExtractor.extract(gene, null, entry.type(), event);
        String sourceEvent;
        if (!gene.equals(CkbConstants.NO_GENE)) {
            sourceEvent = gene + " " + event;
        } else {
            sourceEvent = event;
        }

        Set<ActionableEntry> actionableEntries = actionableEntryFactory.create(entry, sourceEvent, gene);

        EventInterpretation interpretation = ImmutableEventInterpretation.builder()
                .source(source)
                .sourceEvent(sourceEvent)
                .interpretedGene(gene)
                .interpretedEvent(event)
                .interpretedEventType(entry.type())
                .build();

        return toExtractionResult(event, gene, extractionOutput, actionableEntries, interpretation);
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
    private ExtractionResult toExtractionResult(@NotNull String variant, @NotNull String gene, @NotNull EventExtractorOutput output,
            @NotNull Set<ActionableEntry> actionableEntries, @NotNull EventInterpretation interpretation) {

        List<CodonAnnotation> codons = curateCodons(output.codons());

        ImmutableExtractionResult.Builder extractionResultBuilder = actionableEntries.stream()
                .map(event -> ImmutableExtractionResult.builder()
                        .refGenomeVersion(source.refGenomeVersion())
                        .actionableHotspots(ActionableEventFactory.toActionableHotspots(event, output.hotspots()))
                        .actionableCodons(ActionableEventFactory.toActionableRanges(event, codons))
                        .actionableExons(ActionableEventFactory.toActionableRanges(event, output.exons()))
                        .actionableGenes(Stream.of(output.geneLevel(), output.copyNumber())
                                .filter(Objects::nonNull)
                                .map(annotation -> ActionableEventFactory.geneAnnotationToActionableGene(event, annotation))
                                .collect(Collectors.toSet()))
                        .actionableFusions(extractNonNullToSet(output.fusionPair(), event, ActionableEventFactory::toActionableFusion))
                        .actionableCharacteristics(extractNonNullToSet(output.characteristic(),
                                event,
                                ActionableEventFactory::toActionableCharacteristic))
                        .actionableHLA(extractNonNullToSet(output.hla(), event, ActionableEventFactory::toActionableHLa)))
                .reduce(ImmutableExtractionResult.builder(), (ImmutableExtractionResult.Builder a, ImmutableExtractionResult.Builder b) -> {
                    ExtractionResult built = b.build();
                    a.addAllActionableHotspots(built.actionableHotspots());
                    a.addAllActionableCodons(built.actionableCodons());
                    a.addAllActionableExons(built.actionableExons());
                    a.addAllActionableGenes(built.actionableGenes());
                    a.addAllActionableFusions(built.actionableFusions());
                    a.addAllActionableCharacteristics(built.actionableCharacteristics());
                    a.addAllActionableHLA(built.actionableHLA());
                    return a;
                });

        if (generateKnownEvents) {
            extractionResultBuilder.knownHotspots(convertToKnownHotspots(output.hotspots(), variant))
                    .knownCodons(convertToKnownCodons(codons))
                    .knownExons(convertToKnownExons(output.exons()))
                    .knownGenes(output.fusionPair() == null ? convertToKnownGenes(gene) : Collections.emptySet())
                    .knownCopyNumbers(convertToKnownAmpsDels(output.copyNumber()))
                    .knownFusions(convertToKnownFusions(output.fusionPair()));
        }

        return extractionResultBuilder
                .refGenomeVersion(source.refGenomeVersion())
                .addEventInterpretations(interpretation)
                .build();
    }

    @VisibleForTesting
    @Nullable
    static List<CodonAnnotation> curateCodons(@Nullable List<CodonAnnotation> codonAnnotations) {
        return codonAnnotations == null ? null : codonAnnotations.stream().map(codon -> {
            if (codon.gene().equals("BRAF") && codon.inputCodonRank() == 600) {
                return ImmutableCodonAnnotation.builder()
                        .from(codon)
                            .inputTranscript("ENST00000646891")
                            .start(140753335)
                            .end(140753337)
                            .build();
            }
            return codon;
        })
                .collect(Collectors.toList());

    }
    
    @NotNull
    private <T, U> Set<U> convertToKnownSet(@Nullable List<T> rawList, @NotNull Function<T, U> convert,
            @NotNull Function<Set<U>, Set<U>> consolidate) {
        if (rawList == null) {
            return Collections.emptySet();
        }
        Set<U> converted = rawList.stream().map(convert).collect(Collectors.toSet());
        return consolidate.apply(converted);
    }

    @NotNull
    private Set<KnownHotspot> convertToKnownHotspots(@Nullable List<VariantHotspot> hotspots, @NotNull String variant) {
        CkbProteinAnnotationExtractor proteinExtractor = new CkbProteinAnnotationExtractor();
        Function<VariantHotspot, KnownHotspot> convert = hotspot -> ImmutableKnownHotspot.builder()
                .from(hotspot)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .addSources(source)
                .inputProteinAnnotation(proteinExtractor.apply(variant))
                .build();
        return convertToKnownSet(hotspots, convert, HotspotConsolidation::consolidate);
    }

    @NotNull
    private Set<KnownCodon> convertToKnownCodons(@Nullable List<CodonAnnotation> codonAnnotations) {
        Function<CodonAnnotation, KnownCodon> convert = codonAnnotation -> ImmutableKnownCodon.builder()
                .from(codonAnnotation)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .inputTranscript(codonAnnotation.inputTranscript())
                .inputCodonRank(codonAnnotation.inputCodonRank())
                .addSources(source)
                .build();
        return convertToKnownSet(codonAnnotations, convert, CodonConsolidation::consolidate);
    }

    @NotNull
    private Set<KnownExon> convertToKnownExons(@Nullable List<ExonAnnotation> exonAnnotations) {
        Function<ExonAnnotation, KnownExon> convert = exonAnnotation -> ImmutableKnownExon.builder()
                .from(exonAnnotation)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .inputTranscript(exonAnnotation.inputTranscript())
                .inputExonRank(exonAnnotation.inputExonRank())
                .addSources(source)
                .build();
        return convertToKnownSet(exonAnnotations, convert, ExonConsolidation::consolidate);
    }

    @NotNull
    private Set<KnownGene> convertToKnownGenes(@NotNull String gene) {
        if (!gene.equals(CkbConstants.NO_GENE)) {
            return Set.of(ImmutableKnownGene.builder().gene(gene).geneRole(GeneRole.UNKNOWN).addSources(source).build());
        }

        return Collections.emptySet();
    }

    @NotNull
    private Set<KnownCopyNumber> convertToKnownAmpsDels(@Nullable GeneAnnotation copyNumber) {
        Set<KnownCopyNumber> copyNumbers = (copyNumber == null) ? Collections.emptySet() : Set.of(
                ImmutableKnownCopyNumber.builder()
                    .from(copyNumber)
                    .geneRole(GeneRole.UNKNOWN)
                    .proteinEffect(ProteinEffect.UNKNOWN)
                    .addSources(source)
                    .build()
        );

        return CopyNumberConsolidation.consolidate(copyNumbers);
    }

    @NotNull
    private Set<KnownFusion> convertToKnownFusions(@Nullable FusionPair fusion) {
        Set<KnownFusion> fusions = (fusion == null) ? Collections.emptySet()
                : Set.of(ImmutableKnownFusion.builder().from(fusion).proteinEffect(ProteinEffect.UNKNOWN).addSources(source).build());

        return FusionConsolidation.consolidate(fusions);
    }
}

