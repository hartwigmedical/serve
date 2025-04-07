package com.hartwig.serve.sources.ckb;

import static com.hartwig.serve.ServeConfig.LOGGER;
import static com.hartwig.serve.sources.ckb.CkbVariantAnnotator.resolveGeneRole;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hartwig.serve.ckb.classification.CkbConstants;
import com.hartwig.serve.ckb.classification.CkbProteinAnnotationExtractor;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.molecular.common.GeneAlteration;
import com.hartwig.serve.datamodel.molecular.common.GeneRole;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.fusion.FusionPair;
import com.hartwig.serve.datamodel.molecular.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.molecular.fusion.KnownFusion;
import com.hartwig.serve.datamodel.molecular.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.molecular.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.molecular.gene.ImmutableKnownGene;
import com.hartwig.serve.datamodel.molecular.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.molecular.gene.KnownGene;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantAnnotation;
import com.hartwig.serve.datamodel.molecular.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.molecular.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.molecular.range.KnownCodon;
import com.hartwig.serve.datamodel.molecular.range.KnownExon;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.CodonConsolidation;
import com.hartwig.serve.extraction.copynumber.CopyNumberConsolidation;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.exon.ExonConsolidation;
import com.hartwig.serve.extraction.fusion.FusionConsolidation;
import com.hartwig.serve.extraction.variant.KnownHotspotConsolidation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CkbKnownEventsExtractor {

    @NotNull
    public static KnownEvents generateKnownEvents(@NotNull List<ExtractedEvent> allEvents) {
        Set<KnownHotspot> allHotspots = allEvents.stream()
                .flatMap(event -> convertToKnownHotspots(event.eventExtractorOutput().variants(), event.event(), event.variant()).stream())
                .collect(Collectors.toSet());

        Set<KnownCodon> allCodons = allEvents.stream()
                .flatMap(event -> convertToKnownCodons(event.eventExtractorOutput().codons(), event.variant()).stream())
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
    private static Set<KnownHotspot> convertToKnownHotspots(@Nullable List<VariantAnnotation> variants, @NotNull String event,
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
    private static Set<KnownCodon> convertToKnownCodons(@Nullable List<CodonAnnotation> codonAnnotations, @NotNull Variant variant) {
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
    private static Set<KnownExon> convertToKnownExons(@Nullable List<ExonAnnotation> exonAnnotations, @NotNull Variant variant) {
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
    private static Set<KnownGene> convertToKnownGenes(@NotNull String gene, @NotNull Variant variant) {
        if (!gene.equals(CkbConstants.NO_GENE)) {
            return Set.of(ImmutableKnownGene.builder().gene(gene).geneRole(resolveGeneRole(variant)).addSources(Knowledgebase.CKB).build());
        }

        return Collections.emptySet();
    }

    @NotNull
    private static Set<KnownCopyNumber> convertToKnownCopyNumbers(@Nullable GeneAnnotation copyNumber, @NotNull Variant variant) {
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
    private static Set<KnownFusion> convertToKnownFusions(@Nullable FusionPair fusion, @NotNull Variant variant) {
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
    private static <T, U> Set<U> convertToKnownSet(@Nullable List<T> rawList, @NotNull Function<T, U> convert,
            @NotNull Function<Set<U>, Set<U>> consolidate, @NotNull BiFunction<U, Variant, U> annotate, @NotNull Variant variant) {
        if (rawList == null) {
            return Collections.emptySet();
        }
        Set<U> converted = rawList.stream().map(convert).collect(Collectors.toSet());
        return consolidate.apply(converted).stream().map(e -> annotate.apply(e, variant)).filter(e -> {
            if (e instanceof GeneAlteration) {
                return ((GeneAlteration) e).proteinEffect() != ProteinEffect.UNKNOWN;
            } else if (e instanceof KnownFusion) {
                return ((KnownFusion) e).proteinEffect() != ProteinEffect.UNKNOWN;
            }
            LOGGER.warn("{} is not a GeneAlteration or KnownFusion", e.getClass().toString());
            return true;
        }).collect(Collectors.toSet());
    }
}