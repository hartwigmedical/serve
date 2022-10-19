package com.hartwig.serve.extraction;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.common.variant.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.actionability.ActionableEvent;
import com.hartwig.serve.datamodel.actionability.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.actionability.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.actionability.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.actionability.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.actionability.gene.ActionableGene;
import com.hartwig.serve.datamodel.actionability.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.actionability.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.actionability.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.actionability.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.actionability.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.actionability.range.ActionableRange;
import com.hartwig.serve.datamodel.actionability.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.actionability.range.RangeType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.gene.GeneLevelAnnotation;
import com.hartwig.serve.datamodel.gene.GeneLevelEvent;
import com.hartwig.serve.datamodel.range.RangeAnnotation;
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
                    .chromosome(hotspot.chromosome())
                    .position(hotspot.position())
                    .ref(hotspot.ref())
                    .alt(hotspot.alt())
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
        for (RangeAnnotation range : ranges) {
            actionableRanges.add(ImmutableActionableRange.builder()
                    .from(actionableEvent)
                    .chromosome(range.chromosome())
                    .transcript(range.transcript())
                    .start(range.start())
                    .end(range.end())
                    .gene(range.gene())
                    .mutationType(range.mutationType())
                    .rangeType(determineRangeType(range))
                    .rank(range.rank())
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
            case OVER_EXPRESSION: {
                event = GeneLevelEvent.OVEREXPRESSION;
                break;
            }
            case DELETION: {
                event = GeneLevelEvent.DELETION;
                break;
            }
            case UNDER_EXPRESSION: {
                event = GeneLevelEvent.UNDEREXPRESSION;
                break;
            }
            default:
                throw new IllegalStateException("Invalid copy number type: " + copyNumber.type());
        }

        return ImmutableActionableGene.builder().from(actionableEvent).gene(copyNumber.gene()).event(event).build();
    }

    @NotNull
    public static ActionableGene geneLevelEventToActionableGene(@NotNull ActionableEvent actionableEvent,
            @NotNull GeneLevelAnnotation geneLevelEvent) {
        return ImmutableActionableGene.builder().from(actionableEvent).gene(geneLevelEvent.gene()).event(geneLevelEvent.event()).build();
    }

    @NotNull
    public static ActionableFusion toActionableFusion(@NotNull ActionableEvent actionableEvent, @NotNull KnownFusionPair fusion) {
        return ImmutableActionableFusion.builder()
                .from(actionableEvent)
                .geneUp(fusion.geneUp())
                .minExonUp(fusion.minExonUp())
                .maxExonUp(fusion.maxExonUp())
                .geneDown(fusion.geneDown())
                .minExonDown(fusion.minExonDown())
                .maxExonDown(fusion.maxExonDown())
                .build();
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