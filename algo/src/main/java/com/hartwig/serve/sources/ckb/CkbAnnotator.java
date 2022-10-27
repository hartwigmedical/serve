package com.hartwig.serve.sources.ckb;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.variant.Gene;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.fusion.ImmutableKnownFusionPair;
import com.hartwig.serve.datamodel.fusion.KnownFusionPair;
import com.hartwig.serve.datamodel.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.range.CodonAnnotation;
import com.hartwig.serve.datamodel.range.ExonAnnotation;
import com.hartwig.serve.datamodel.range.ImmutableCodonAnnotation;
import com.hartwig.serve.datamodel.range.ImmutableExonAnnotation;
import com.hartwig.serve.extraction.EventExtractorOutput;
import com.hartwig.serve.extraction.ImmutableEventExtractorOutput;
import com.hartwig.serve.extraction.gene.ImmutableGeneAnnotationImpl;
import com.hartwig.serve.extraction.hotspot.ImmutableVariantHotspotImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CkbAnnotator {

    private static final Logger LOGGER = LogManager.getLogger(CkbAnnotator.class);

    private CkbAnnotator() {
    }

    @NotNull
    public static EventExtractorOutput annotate(@NotNull EventExtractorOutput extract, @NotNull Variant variant) {
        return ImmutableEventExtractorOutput.builder()
                .from(extract)
                .hotspots(annotateHotspots(extract.hotspots(), variant))
                .codons(annotateCodons(extract.codons(), variant))
                .exons(annotateExons(extract.exons(), variant))
                .geneAnnotation(annotateGeneAnnotation(extract.geneAnnotation(), variant))
                .knownCopyNumber(annotateKnownCopyNumber(extract.knownCopyNumber(), variant))
                .knownFusionPair(annotateKnownFusionPair(extract.knownFusionPair(), variant))
                .build();
    }

    @Nullable
    private static List<VariantHotspot> annotateHotspots(@Nullable List<VariantHotspot> hotspots, @NotNull Variant variant) {
        if (hotspots == null) {
            return null;
        }

        List<VariantHotspot> annotated = Lists.newArrayList();
        for (VariantHotspot hotspot : hotspots) {
            annotated.add(ImmutableVariantHotspotImpl.builder()
                    .from(hotspot)
                    .gene(variant.gene().geneSymbol())
                    .geneRole(resolveGeneRole(variant.gene()))
                    .proteinEffect(resolveProteinEffect(variant))
                    .build());
        }
        return annotated;
    }

    @Nullable
    private static List<CodonAnnotation> annotateCodons(@Nullable List<CodonAnnotation> codons, @NotNull Variant variant) {
        if (codons == null) {
            return null;
        }

        List<CodonAnnotation> annotated = Lists.newArrayList();
        for (CodonAnnotation codon : codons) {
            annotated.add(ImmutableCodonAnnotation.builder()
                    .from(codon)
                    .gene(variant.gene().geneSymbol())
                    .geneRole(resolveGeneRole(variant.gene()))
                    .proteinEffect(resolveProteinEffect(variant))
                    .build());
        }
        return annotated;
    }

    @Nullable
    private static List<ExonAnnotation> annotateExons(@Nullable List<ExonAnnotation> exons, @NotNull Variant variant) {
        if (exons == null) {
            return null;
        }

        List<ExonAnnotation> annotated = Lists.newArrayList();
        for (ExonAnnotation exon : exons) {
            annotated.add(ImmutableExonAnnotation.builder()
                    .from(exon)
                    .gene(variant.gene().geneSymbol())
                    .geneRole(resolveGeneRole(variant.gene()))
                    .proteinEffect(resolveProteinEffect(variant))
                    .build());
        }
        return annotated;
    }

    @Nullable
    private static GeneAnnotation annotateGeneAnnotation(@Nullable GeneAnnotation geneAnnotation, @NotNull Variant variant) {
        if (geneAnnotation == null) {
            return null;
        }

        return ImmutableGeneAnnotationImpl.builder()
                .from(geneAnnotation)
                .gene(variant.gene().geneSymbol())
                .geneRole(resolveGeneRole(variant.gene()))
                .proteinEffect(resolveProteinEffect(variant))
                .build();
    }

    @Nullable
    private static KnownCopyNumber annotateKnownCopyNumber(@Nullable KnownCopyNumber knownCopyNumber, @NotNull Variant variant) {
        if (knownCopyNumber == null) {
            return null;
        }

        return ImmutableKnownCopyNumber.builder()
                .from(knownCopyNumber)
                .gene(variant.gene().geneSymbol())
                .geneRole(resolveGeneRole(variant.gene()))
                .proteinEffect(resolveProteinEffect(variant))
                .build();
    }

    @Nullable
    private static KnownFusionPair annotateKnownFusionPair(@Nullable KnownFusionPair knownFusionPair, @NotNull Variant variant) {
        if (knownFusionPair == null) {
            return knownFusionPair;
        }

        return ImmutableKnownFusionPair.builder().from(knownFusionPair).proteinEffect(resolveProteinEffect(variant)).build();
    }

    @NotNull
    private static GeneRole resolveGeneRole(@NotNull Gene gene) {
        String role = gene.geneRole().toLowerCase();
        if (role.contains("both")) {
            return GeneRole.BOTH;
        } else if (role.contains("oncogene")) {
            return GeneRole.ONCO;
        } else if (role.contains("tumor suppressor")) {
            return GeneRole.TSG;
        } else if (role.contains("unknown") || role.contains("na")) {
            return GeneRole.UNKNOWN;
        }

        LOGGER.warn("Unrecognized CKB gene role: " + gene.geneRole());
        return GeneRole.UNKNOWN;
    }

    @NotNull
    private static ProteinEffect resolveProteinEffect(@NotNull Variant variant) {
        String effect = variant.proteinEffect();
        if (effect == null) {
            LOGGER.warn("No CKB protein effect recorded for {}", variant);
            return ProteinEffect.UNKNOWN;
        }

        switch (effect) {
            case "unknown":
                return ProteinEffect.UNKNOWN;
            case "loss of function":
                return ProteinEffect.LOSS_OF_FUNCTION;
            case "no effect - predicted":
                return ProteinEffect.NO_EFFECT_PREDICTED;
            case "loss of function - predicted":
                return ProteinEffect.LOSS_OF_FUNCTION_PREDICTED;
            case "gain of function":
                return ProteinEffect.GAIN_OF_FUNCTION;
            case "gain of function - predicted":
                return ProteinEffect.GAIN_OF_FUNCTION_PREDICTED;
            case "no effect":
                return ProteinEffect.NO_EFFECT;
            default: {
                LOGGER.warn("Unrecognized CKB protein effect: " + variant.proteinEffect());
                return ProteinEffect.UNKNOWN;
            }
        }
    }
}
