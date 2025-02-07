package com.hartwig.serve.sources.ckb;

import static com.hartwig.serve.sources.ckb.CkbVariantAnnotator.resolveGeneRole;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.hartwig.serve.ckb.classification.CkbConstants;
import com.hartwig.serve.ckb.classification.CkbProteinAnnotationExtractor;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.ImmutableKnownEvents;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
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
import com.hartwig.serve.datamodel.molecular.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.molecular.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.molecular.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.molecular.range.KnownCodon;
import com.hartwig.serve.datamodel.molecular.range.KnownExon;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.CodonConsolidation;
import com.hartwig.serve.extraction.copynumber.CopyNumberConsolidation;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.exon.ExonConsolidation;
import com.hartwig.serve.extraction.fusion.FusionConsolidation;
import com.hartwig.serve.extraction.hotspot.HotspotConsolidation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CkbKnownEventsGenerator {
    @NotNull
    public static KnownEvents generateKnownEvents(@NotNull EventExtractorOutput extractorOutput, boolean efficacyEvidencesIsEmpty,
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
    private static <T, U> Set<U> convertToKnownSet(@Nullable List<T> rawList, @NotNull Function<T, U> convert,
            @NotNull Function<Set<U>, Set<U>> consolidate, @NotNull BiFunction<U, Variant, U> annotate, @NotNull Variant variant) {
        if (rawList == null) {
            return Collections.emptySet();
        }
        Set<U> converted = rawList.stream().map(convert).collect(Collectors.toSet());
        return consolidate.apply(converted).stream().map(e -> annotate.apply(e, variant)).collect(Collectors.toSet());
    }

    @NotNull
    private static Set<KnownHotspot> convertToKnownHotspots(@Nullable List<VariantHotspot> hotspots, @NotNull String event,
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
}
