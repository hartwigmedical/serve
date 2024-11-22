package com.hartwig.serve.sources.ckb;

import com.hartwig.serve.ckb.datamodel.variant.Gene;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.datamodel.molecular.common.GeneRole;
import com.hartwig.serve.datamodel.molecular.common.ProteinEffect;
import com.hartwig.serve.datamodel.molecular.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.molecular.fusion.KnownFusion;
import com.hartwig.serve.datamodel.molecular.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.molecular.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.molecular.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.molecular.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.molecular.range.KnownCodon;
import com.hartwig.serve.datamodel.molecular.range.KnownExon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CkbVariantAnnotator {

    private static final Logger LOGGER = LogManager.getLogger(CkbVariantAnnotator.class);

    @NotNull
    public static KnownHotspot annotateHotspot(@NotNull KnownHotspot hotspot, @NotNull Variant variant) {
        return ImmutableKnownHotspot.copyOf(hotspot)
                .withGeneRole(resolveGeneRole(variant))
                .withProteinEffect(resolveProteinEffect(variant))
                .withAssociatedWithDrugResistance(resolveAssociatedWithDrugResistance(variant));
    }

    @NotNull
    public static KnownCodon annotateCodon(@NotNull KnownCodon codon, @NotNull Variant variant) {
        return ImmutableKnownCodon.copyOf(codon)
                .withGeneRole(resolveGeneRole(variant))
                .withProteinEffect(resolveProteinEffect(variant))
                .withAssociatedWithDrugResistance(resolveAssociatedWithDrugResistance(variant));
    }

    @NotNull
    public static KnownExon annotateExon(@NotNull KnownExon exon, @NotNull Variant variant) {
        return ImmutableKnownExon.copyOf(exon)
                .withGeneRole(resolveGeneRole(variant))
                .withProteinEffect(resolveProteinEffect(variant))
                .withAssociatedWithDrugResistance(resolveAssociatedWithDrugResistance(variant));
    }

    @NotNull
    public static KnownCopyNumber annotateCopyNumber(@NotNull KnownCopyNumber copyNumber, @NotNull Variant variant) {
        return ImmutableKnownCopyNumber.copyOf(copyNumber)
                .withGeneRole(resolveGeneRole(variant))
                .withProteinEffect(resolveProteinEffect(variant))
                .withAssociatedWithDrugResistance(resolveAssociatedWithDrugResistance(variant));
    }

    @NotNull
    public static KnownFusion annotateFusion(@NotNull KnownFusion fusion, @NotNull Variant variant) {
        return ImmutableKnownFusion.copyOf(fusion)
                .withProteinEffect(resolveProteinEffect(variant))
                .withAssociatedWithDrugResistance(resolveAssociatedWithDrugResistance(variant));
    }

    @NotNull
    public static GeneRole resolveGeneRole(@NotNull Variant variant) {
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
