package com.hartwig.serve.extraction;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.common.GeneAlteration;
import com.hartwig.serve.datamodel.common.GeneRole;
import com.hartwig.serve.datamodel.common.ProteinEffect;
import com.hartwig.serve.datamodel.common.Variant;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.gene.GeneLevelEvent;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.range.RangeAnnotation;
import com.hartwig.serve.datamodel.range.RangeType;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.extraction.copynumber.KnownCopyNumber;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.exon.ImmutableExonAnnotation;
import com.hartwig.serve.extraction.fusion.KnownFusionPair;
import com.hartwig.serve.extraction.immuno.ImmunoHLA;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ActionableEventFactory {

    private ActionableEventFactory() {
    }

    @NotNull
    public static Set<ActionableHotspot> toActionableHotspots(@NotNull ActionableEvent actionableEvent,
            @Nullable List<VariantHotspot> hotspotList) {
        if (hotspotList == null) {
            return Sets.newHashSet();
        }

        Set<ActionableHotspot> actionableHotspots = Sets.newHashSet();
        for (VariantHotspot hotspot : hotspotList) {
            actionableHotspots.add(ImmutableActionableHotspot.builder()
                    .from(actionableEvent)
                    .from((Variant) hotspot)
                    .from((GeneAlteration) hotspot)
                    .build());
        }

        return actionableHotspots;
    }

    @NotNull
    public static Set<ActionableRange> toActionableRanges(@NotNull ActionableEvent actionableEvent,
            @Nullable List<? extends RangeAnnotation> ranges) {
        if (ranges == null) {
            return Sets.newHashSet();
        }

        Set<ActionableRange> actionableRanges = Sets.newHashSet();
        for (RangeAnnotation rangeAnnotation : ranges) {
            actionableRanges.add(ImmutableActionableRange.builder()
                    .from(actionableEvent)
                    .from(rangeAnnotation)
                    .rangeType(determineRangeType(rangeAnnotation))
                    .build());
        }

        return actionableRanges;
    }

    @NotNull
    private static RangeType determineRangeType(@NotNull RangeAnnotation annotation) {
        if (annotation instanceof CodonAnnotation || annotation instanceof ImmutableCodonAnnotation) {
            return RangeType.CODON;
        } else if (annotation instanceof ExonAnnotation || annotation instanceof ImmutableExonAnnotation) {
            return RangeType.EXON;
        }

        throw new IllegalStateException("Could not determine range type for: " + annotation);
    }

    @NotNull
    public static ActionableGene copyNumberToActionableGene(@NotNull ActionableEvent actionableEvent, @NotNull KnownCopyNumber copyNumber) {
        GeneLevelEvent event;
        switch (copyNumber.type()) {
            case AMPLIFICATION: {
                event = GeneLevelEvent.AMPLIFICATION;
                break;
            }
            case OVEREXPRESSION: {
                event = GeneLevelEvent.OVEREXPRESSION;
                break;
            }
            case DELETION: {
                event = GeneLevelEvent.DELETION;
                break;
            }
            case UNDEREXPRESSION: {
                event = GeneLevelEvent.UNDEREXPRESSION;
                break;
            }
            default:
                throw new IllegalStateException("Invalid copy number type: " + copyNumber.type());
        }

        // TODO Capture gene role and protein effect.
        return ImmutableActionableGene.builder()
                .from(actionableEvent)
                .gene(copyNumber.gene())
                .geneRole(GeneRole.UNKNOWN)
                .proteinEffect(ProteinEffect.UNKNOWN)
                .event(event)
                .build();
    }

    @NotNull
    public static ActionableGene geneAnnotationToActionableGene(@NotNull ActionableEvent actionableEvent,
            @NotNull GeneAnnotation geneAnnotation) {
        return ImmutableActionableGene.builder().from(actionableEvent).from(geneAnnotation).event(geneAnnotation.event()).build();
    }

    @NotNull
    public static ActionableFusion toActionableFusion(@NotNull ActionableEvent actionableEvent, @NotNull KnownFusionPair fusion) {
        return ImmutableActionableFusion.builder().from(actionableEvent).from(fusion).build();
    }

    @NotNull
    public static ActionableCharacteristic toActionableCharacteristic(@NotNull ActionableEvent actionableEvent,
            @NotNull TumorCharacteristic characteristic) {
        return ImmutableActionableCharacteristic.builder()
                .from(actionableEvent)
                .name(characteristic.name())
                .comparator(characteristic.comparator())
                .cutoff(characteristic.cutoff())
                .build();
    }

    @NotNull
    public static ActionableHLA toActionableHLa(@NotNull ActionableEvent actionableEvent, @NotNull ImmunoHLA hla) {
        return ImmutableActionableHLA.builder().from(actionableEvent).hlaType(hla.immunoHLA()).build();
    }
}