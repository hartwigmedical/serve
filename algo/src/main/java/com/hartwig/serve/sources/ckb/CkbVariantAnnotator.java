package com.hartwig.serve.sources.ckb;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.variant.Gene;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.gene.ImmutableKnownGene;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.datamodel.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CkbVariantAnnotator {

    private static final Logger LOGGER = LogManager.getLogger(CkbVariantAnnotator.class);

    private CkbVariantAnnotator() {
    }

    @NotNull
    public static ExtractionResult annotate(@NotNull ExtractionResult result, @NotNull Variant variant) {
        return ImmutableExtractionResult.builder()
                .from(result)
                .knownHotspots(annotateHotspots(result.knownHotspots(), variant))
                .knownCodons(annotateCodons(result.knownCodons(), variant))
                .knownExons(annotateExons(result.knownExons(), variant))
                .knownGenes(annotateGenes(result, variant))
                .knownCopyNumbers(annotateCopyNumbers(result.knownCopyNumbers(), variant))
                .knownFusions(annotateFusions(result.knownFusions(), variant))
                .build();
    }

    @NotNull
    private static Set<KnownHotspot> annotateHotspots(@NotNull Set<KnownHotspot> hotspots, @NotNull Variant variant) {
        Set<KnownHotspot> annotated = Sets.newHashSet();
        for (KnownHotspot hotspot : hotspots) {
            annotated.add(ImmutableKnownHotspot.builder()
                    .from(hotspot)
                    .geneRole(resolveGeneRole(variant))
                    .proteinEffect(resolveProteinEffect(variant))
                    .associatedWithDrugResistance(resolveAssociatedWithDrugResistance(variant))
                    .build());
        }
        return annotated;
    }

    @NotNull
    private static Set<KnownCodon> annotateCodons(@NotNull Set<KnownCodon> codons, @NotNull Variant variant) {
        Set<KnownCodon> annotated = Sets.newHashSet();
        for (KnownCodon codon : codons) {
            annotated.add(ImmutableKnownCodon.builder()
                    .from(codon)
                    .geneRole(resolveGeneRole(variant))
                    .proteinEffect(resolveProteinEffect(variant))
                    .associatedWithDrugResistance(resolveAssociatedWithDrugResistance(variant))
                    .build());
        }
        return annotated;
    }

    @NotNull
    private static Set<KnownExon> annotateExons(@NotNull Set<KnownExon> exons, @NotNull Variant variant) {
        Set<KnownExon> annotated = Sets.newHashSet();
        for (KnownExon exon : exons) {
            annotated.add(ImmutableKnownExon.builder()
                    .from(exon)
                    .geneRole(resolveGeneRole(variant))
                    .proteinEffect(resolveProteinEffect(variant))
                    .associatedWithDrugResistance(resolveAssociatedWithDrugResistance(variant))
                    .build());
        }
        return annotated;
    }

    @NotNull
    private static Set<KnownGene> annotateGenes(@NotNull ExtractionResult result, @NotNull Variant variant) {
        return result.knownGenes()
                .stream()
                .map(gene -> ImmutableKnownGene.copyOf(gene).withGeneRole(resolveGeneRole(variant)))
                .collect(Collectors.toSet());
    }

    @NotNull
    private static Set<KnownCopyNumber> annotateCopyNumbers(@NotNull Set<KnownCopyNumber> copyNumbers, @NotNull Variant variant) {
        Set<KnownCopyNumber> annotated = Sets.newHashSet();
        for (KnownCopyNumber copyNumber : copyNumbers) {
            annotated.add(ImmutableKnownCopyNumber.builder()
                    .from(copyNumber)
                    .geneRole(resolveGeneRole(variant))
                    .proteinEffect(resolveProteinEffect(variant))
                    .associatedWithDrugResistance(resolveAssociatedWithDrugResistance(variant))
                    .build());
        }
        return annotated;
    }

    @NotNull
    private static Set<KnownFusion> annotateFusions(@NotNull Set<KnownFusion> fusions, @NotNull Variant variant) {
        Set<KnownFusion> annotated = Sets.newHashSet();
        for (KnownFusion fusion : fusions) {
            annotated.add(ImmutableKnownFusion.builder()
                    .from(fusion)
                    .proteinEffect(resolveProteinEffect(variant))
                    .associatedWithDrugResistance(resolveAssociatedWithDrugResistance(variant))
                    .build());
        }
        return annotated;
    }

    @NotNull
    private static GeneRole resolveGeneRole(@NotNull Variant variant) {
        Gene gene = variant.gene();
        if (gene == null) {
            throw new IllegalStateException("Cannot resolve gene role for variant with no gene: " + variant);
        }

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

        LOGGER.warn("Unrecognized CKB gene role: {}", gene.geneRole());
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
                LOGGER.warn("Unrecognized CKB protein effect: {}", variant.proteinEffect());
                return ProteinEffect.UNKNOWN;
            }
        }
    }

    @Nullable
    private static Boolean resolveAssociatedWithDrugResistance(@NotNull Variant variant) {
        String associatedWithDrugResistance = variant.associatedWithDrugResistance();
        if (associatedWithDrugResistance == null) {
            return null;
        }

        if (associatedWithDrugResistance.equals("Y")) {
            return true;
        }

        LOGGER.warn("Unrecognized CKB associated with drug resistance: {}", associatedWithDrugResistance);
        return null;
    }
}
