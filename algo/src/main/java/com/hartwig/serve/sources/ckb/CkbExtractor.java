package com.hartwig.serve.sources.ckb;

import static com.hartwig.serve.sources.ckb.CkbVariantAnnotator.resolveGeneRole;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.classification.CkbConstants;
import com.hartwig.serve.ckb.classification.CkbEventAndGeneExtractor;
import com.hartwig.serve.ckb.classification.CkbEventTypeExtractor;
import com.hartwig.serve.ckb.classification.CkbProteinAnnotationExtractor;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.ImmutableActionableEventImpl;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.molecular.ActionableEvent;
import com.hartwig.serve.datamodel.molecular.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.molecular.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.molecular.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.molecular.common.GeneRole;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.molecular.fusion.FusionPair;
import com.hartwig.serve.datamodel.molecular.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.molecular.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.molecular.fusion.KnownFusion;
import com.hartwig.serve.datamodel.molecular.gene.ActionableGene;
import com.hartwig.serve.datamodel.molecular.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.molecular.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.molecular.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.molecular.gene.ImmutableKnownGene;
import com.hartwig.serve.datamodel.molecular.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.molecular.gene.KnownGene;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantAnnotation;
import com.hartwig.serve.datamodel.molecular.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.molecular.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.molecular.range.ActionableRange;
import com.hartwig.serve.datamodel.molecular.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.molecular.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.molecular.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.molecular.range.KnownCodon;
import com.hartwig.serve.datamodel.molecular.range.KnownExon;
import com.hartwig.serve.datamodel.molecular.range.RangeAnnotation;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ExtractedEvent;
import com.hartwig.serve.extraction.ExtractionFunctions;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableEventExtractorOutput;
import com.hartwig.serve.extraction.ImmutableExtractedEvent;
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
import com.hartwig.serve.extraction.immuno.ImmunoHLA;
import com.hartwig.serve.extraction.variant.KnownHotspotConsolidation;
import com.hartwig.serve.util.ProgressTracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbExtractor {

    private static final Logger LOGGER = LogManager.getLogger(CkbExtractor.class);
    private static final String VARIANT_DELIMITER = ",";
    private static final String GENE_DELIMITER = ",";

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

        if (entry.type() == EventType.UNKNOWN) {
            LOGGER.warn("No event type known for CKB profile ID '{}': '{}'", entry.profileId(), entry.profileName());
            return null;
        }

        if (entry.variants().size() == 1) {
            return extractSimpleEvent(entry);
        } else {
            return extractCombinedEvent(entry);
        }
    }

    @NotNull
    private ExtractionResult extractSimpleEvent(@NotNull CkbEntry entry) {
        Variant variant = entry.variants().get(0);
        String gene = CkbEventAndGeneExtractor.extractGene(variant);
        String event = CkbEventAndGeneExtractor.extractEvent(variant);

        EventExtractorOutput extractionOutput = curateCodons(eventExtractor.extract(gene, null, entry.type(), event));
        String sourceEvent = gene.equals(CkbConstants.NO_GENE) ? event : gene + " " + event;

        EventInterpretation interpretation = ImmutableEventInterpretation.builder()
                .source(Knowledgebase.CKB)
                .sourceEvent(sourceEvent)
                .interpretedGene(gene)
                .interpretedEvent(event)
                .interpretedEventType(entry.type())
                .build();

        List<ExtractedEvent> allEvents = extractEvents(entry);
        Set<MolecularCriterium> molecularCriteria = createMolecularCriteria(extractionOutput, sourceEvent, entry);

        Set<EfficacyEvidence> efficacyEvidences = efficacyEvidenceFactory.create(entry, molecularCriteria, sourceEvent, gene);
        Set<ActionableTrial> actionableTrials = actionableTrialFactory.create(entry, molecularCriteria, sourceEvent, gene);

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.CKB.refGenomeVersion())
                .eventInterpretations(Set.of(interpretation))
                .knownEvents(generateKnownEvents(allEvents, efficacyEvidences.isEmpty()))
                .evidences(efficacyEvidences)
                .trials(actionableTrials)
                .build();
    }

    @NotNull
    private ExtractionResult extractCombinedEvent(@NotNull CkbEntry entry) {

        EventInterpretation interpretation = ImmutableEventInterpretation.builder()
                .source(Knowledgebase.CKB)
                .sourceEvent("Multiple " + concat(entry.variants()))
                .interpretedGene("Multiple")
                .interpretedEvent(concat(entry.variants()))
                .interpretedEventType(entry.type())
                .build();

        List<ExtractedEvent> extractedEvents = extractEvents(entry);
        String combinedEvent = combineEvents(extractedEvents);
        String combinedGenes = combineGenes(extractedEvents);

        MolecularCriterium molecularCriterium = CkbMolecularCriteriaExtractor.createMolecularCriterium(entry, extractedEvents);

        Set<EfficacyEvidence> efficacyEvidences =
                efficacyEvidenceFactory.create(entry, Set.of(molecularCriterium), combinedEvent, combinedGenes);

        return ImmutableExtractionResult.builder()
                .refGenomeVersion(Knowledgebase.CKB.refGenomeVersion())
                .eventInterpretations(Set.of(interpretation))
                .knownEvents(generateKnownEvents(extractedEvents, efficacyEvidences.isEmpty()))
                .evidences(efficacyEvidences)
                .trials(Set.of())
                .build();
    }

    @NotNull
    private static String combineEvents(@NotNull List<ExtractedEvent> events) {
        return events.stream()
                .map(e -> {
                    String event = e.event();
                    String gene = e.gene();
                    return gene.equals(CkbConstants.NO_GENE) ? event : gene + " " + event;
                })
                .collect(Collectors.joining(VARIANT_DELIMITER));
    }

    @NotNull
    private static String combineGenes(@NotNull List<ExtractedEvent> events) {
        return events.stream()
                .map(ExtractedEvent::gene)
                .collect(Collectors.joining(GENE_DELIMITER));
    }

    @NotNull
    private List<ExtractedEvent> extractEvents(@NotNull CkbEntry entry) {
        // TODO if one event returns null, we are still returning the others. Should we reject the whole entry?
        return entry.variants().stream()
                .map(this::extractEvent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Nullable
    private ExtractedEvent extractEvent(@NotNull Variant variant) {
        EventType eventType = CkbEventTypeExtractor.classify(variant);

        if (eventType == EventType.COMBINED) {
            throw new IllegalStateException("Should not have combined event for single variant: " + variant.fullName());
        } else if (eventType == EventType.UNKNOWN) {
            LOGGER.warn("No known event type for variant: '{}'", variant.fullName());
            return null;
        }

        String event = CkbEventAndGeneExtractor.extractEvent(variant);
        String gene = CkbEventAndGeneExtractor.extractGene(variant);

        EventExtractorOutput eventExtractorOutput =
                CkbMolecularCriteriaExtractor.curateCodons(eventExtractor.extract(gene, null, eventType, event));

        return ImmutableExtractedEvent.builder()
                .gene(gene)
                .event(event)
                .variant(variant)
                .eventType(eventType)
                .eventExtractorOutput(eventExtractorOutput)
                .build();
    }

    @NotNull
    private static String concat(@NotNull List<Variant> variants) {
        return variants.stream().map(Variant::variant).collect(Collectors.joining(VARIANT_DELIMITER));
    }

    @NotNull
    private KnownEvents generateKnownEvents(@NotNull List<ExtractedEvent> allEvents, boolean efficacyEvidencesIsEmpty) {
        Set<KnownHotspot> allHotspots = allEvents.stream()
                .flatMap(event -> convertToKnownHotspots(event.eventExtractorOutput().variants(), event.event(), event.variant()).stream())
                .collect(Collectors.toSet());

        Set<KnownCodon> allCodons = allEvents.stream()
                .flatMap(event -> convertToKnownCodons(efficacyEvidencesIsEmpty
                        ? Collections.emptyList()
                        : event.eventExtractorOutput().codons(), event.variant()).stream())
                .collect(Collectors.toSet());

        Set<KnownExon> allExons = allEvents.stream()
                .flatMap(event -> convertToKnownExons(event.eventExtractorOutput().exons(), event.variant()).stream())
                .collect(Collectors.toSet());

        Set<KnownGene> allGenes = allEvents.stream()
                .flatMap(event -> event.eventExtractorOutput().fusionPair() == null ? convertToKnownGenes(event.gene(),
                        event.variant()).stream() : Stream.empty())
                .collect(Collectors.toSet());

        Set<KnownCopyNumber> allCopyNumbers = allEvents.stream()
                .flatMap(event -> convertToKnownCopyNumbers(event.eventExtractorOutput().copyNumber(), event.variant()).stream())
                .collect(Collectors.toSet());

        Set<KnownFusion> allFusions = allEvents.stream()
                .flatMap(event -> convertToKnownFusions(event.eventExtractorOutput().fusionPair(), event.variant()).stream())
                .collect(Collectors.toSet());

        return ImmutableKnownEvents.builder()
                .hotspots(allHotspots)
                .codons(allCodons)
                .exons(allExons)
                .genes(allGenes)
                .copyNumbers(allCopyNumbers)
                .fusions(allFusions)
                .build();
    }

    @NotNull
    private Set<MolecularCriterium> createMolecularCriteria(@NotNull EventExtractorOutput extractionOutput, @NotNull String sourceEvent,
            @NotNull CkbEntry entry) {
        ActionableEvent actionableEvent = toActionableEvent(sourceEvent, entry);

        Set<MolecularCriterium> molecularCriteria = Sets.newHashSet();

        addHotspotsToCriteria(extractionOutput, actionableEvent, molecularCriteria);
        addCodonsToCriteria(extractionOutput, actionableEvent, molecularCriteria);
        addExonsToCriteria(extractionOutput, actionableEvent, molecularCriteria);
        addGenesToCriteria(extractionOutput, actionableEvent, molecularCriteria);
        addFusionsToCriteria(extractionOutput, actionableEvent, molecularCriteria);
        addCharacteristicsToCriteria(extractionOutput, actionableEvent, molecularCriteria);
        addHlaToCriteria(extractionOutput, actionableEvent, molecularCriteria);

        return molecularCriteria;
    }

    private void addHotspotsToCriteria(@NotNull EventExtractorOutput extractionOutput, @NotNull ActionableEvent actionableEvent,
            @NotNull Set<MolecularCriterium> molecularCriteria) {
        if (extractionOutput.variants() != null) {
            molecularCriteria.add(ImmutableMolecularCriterium.builder()
                    .addHotspots(ImmutableActionableHotspot.builder().from(actionableEvent).variants(extractionOutput.variants()).build())
                    .build());
        }
    }

    private void addCodonsToCriteria(@NotNull EventExtractorOutput extractionOutput, @NotNull ActionableEvent actionableEvent,
            @NotNull Set<MolecularCriterium> molecularCriteria) {
        if (extractionOutput.codons() != null) {
            Set<ActionableRange> codons = extractActionableRanges(extractionOutput.codons(), actionableEvent);
            for (ActionableRange codon : codons) {
                molecularCriteria.add(ImmutableMolecularCriterium.builder().codons(Set.of(codon)).build());
            }
        }
    }

    private void addExonsToCriteria(@NotNull EventExtractorOutput extractionOutput, @NotNull ActionableEvent actionableEvent,
            @NotNull Set<MolecularCriterium> molecularCriteria) {
        if (extractionOutput.exons() != null) {
            Set<ActionableRange> exons = extractActionableRanges(extractionOutput.exons(), actionableEvent);
            for (ActionableRange exon : exons) {
                molecularCriteria.add(ImmutableMolecularCriterium.builder().exons(Set.of(exon)).build());
            }
        }
    }

    private void addGenesToCriteria(@NotNull EventExtractorOutput extractionOutput, @NotNull ActionableEvent actionableEvent,
            @NotNull Set<MolecularCriterium> molecularCriteria) {
        Set<ActionableGene> genes = Stream.of(extractionOutput.geneLevel(), extractionOutput.copyNumber())
                .filter(Objects::nonNull)
                .map(annotation -> extractActionableGenes(annotation, actionableEvent))
                .collect(Collectors.toSet());
        if (!genes.isEmpty()) {
            molecularCriteria.add(ImmutableMolecularCriterium.builder().genes(genes).build());
        }
    }

    private void addFusionsToCriteria(@NotNull EventExtractorOutput extractionOutput, @NotNull ActionableEvent actionableEvent,
            @NotNull Set<MolecularCriterium> molecularCriteria) {
        if (extractionOutput.fusionPair() != null) {
            Set<ActionableFusion> fusions = extractActionableFusions(extractionOutput.fusionPair(), actionableEvent);
            molecularCriteria.add(ImmutableMolecularCriterium.builder().fusions(fusions).build());
        }
    }

    private void addCharacteristicsToCriteria(@NotNull EventExtractorOutput extractionOutput, @NotNull ActionableEvent actionableEvent,
            @NotNull Set<MolecularCriterium> molecularCriteria) {
        if (extractionOutput.characteristic() != null) {
            Set<ActionableCharacteristic> characteristics =
                    extractActionableCharacteristic(extractionOutput.characteristic(), actionableEvent);
            molecularCriteria.add(ImmutableMolecularCriterium.builder().characteristics(characteristics).build());
        }
    }

    private void addHlaToCriteria(@NotNull EventExtractorOutput extractionOutput, @NotNull ActionableEvent actionableEvent,
            @NotNull Set<MolecularCriterium> molecularCriteria) {
        if (extractionOutput.hla() != null) {
            Set<ActionableHLA> hla = extractActionableHLA(extractionOutput.hla(), actionableEvent);
            molecularCriteria.add(ImmutableMolecularCriterium.builder().hla(hla).build());
        }
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
    private Set<KnownHotspot> convertToKnownHotspots(@Nullable List<VariantAnnotation> variants, @NotNull String event,
            @NotNull Variant variant) {
        CkbProteinAnnotationExtractor proteinExtractor = new CkbProteinAnnotationExtractor();
        Function<VariantAnnotation, KnownHotspot> convert = variantAnnotation -> ImmutableKnownHotspot.builder()
                .from(variantAnnotation)
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .addSources(Knowledgebase.CKB)
                .inputProteinAnnotation(proteinExtractor.apply(event))
                .build();

        return convertToKnownSet(variants, convert, KnownHotspotConsolidation::consolidate, CkbVariantAnnotator::annotateHotspot, variant);
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
    private static Set<ActionableCharacteristic> extractActionableCharacteristic(@Nullable TumorCharacteristic characteristic,
            @NotNull ActionableEvent actionableEvent) {
        return Set.of(ImmutableActionableCharacteristic.builder().from(characteristic).from(actionableEvent).build());
    }

    @NotNull
    private static Set<ActionableRange> extractActionableRanges(@NotNull List<? extends RangeAnnotation> ranges,
            @NotNull ActionableEvent actionableEvent) {
        return ranges.stream()
                .map(range -> ImmutableActionableRange.builder().from(range).from(actionableEvent).build())
                .collect(Collectors.toSet());
    }

    @NotNull
    private static Set<ActionableHLA> extractActionableHLA(@Nullable ImmunoHLA hla, @NotNull ActionableEvent actionableEvent) {
        return Set.of(ImmutableActionableHLA.builder().from(hla).from(actionableEvent).build());
    }

    @NotNull
    private static Set<ActionableFusion> extractActionableFusions(@Nullable FusionPair fusionPair,
            @NotNull ActionableEvent actionableEvent) {
        return Set.of(ImmutableActionableFusion.builder().from(fusionPair).from(actionableEvent).build());
    }

    @NotNull
    public static ActionableGene extractActionableGenes(@NotNull GeneAnnotation geneAnnotation, @NotNull ActionableEvent actionableEvent) {
        return ImmutableActionableGene.builder().from(geneAnnotation).from(actionableEvent).build();
    }

    @NotNull
    private static ActionableEvent toActionableEvent(@NotNull String sourceEvent, @NotNull CkbEntry entry) {
        String sourceUrl = "https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=" + entry.profileId();
        LocalDate sourceDate = entry.createDate();
        return ImmutableActionableEventImpl.builder().sourceDate(sourceDate).sourceEvent(sourceEvent).sourceUrls(Set.of(sourceUrl)).build();
    }
}

