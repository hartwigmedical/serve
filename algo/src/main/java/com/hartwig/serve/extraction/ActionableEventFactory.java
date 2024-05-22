package com.hartwig.serve.extraction;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.FusionPair;
import com.hartwig.serve.datamodel.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.VariantHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.range.RangeAnnotation;
import com.hartwig.serve.extraction.immuno.ImmunoHLA;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ActionableEventFactory {

    private ActionableEventFactory() {
    }

    @NotNull
    public static Set<ActionableHotspot> toActionableHotspots(@NotNull ActionableEvent actionableEvent,
            @Nullable List<VariantHotspot> hotspots) {
        if (hotspots == null) {
            return Sets.newHashSet();
        }

        Set<ActionableHotspot> actionableHotspots = Sets.newHashSet();
        for (VariantHotspot hotspot : hotspots) {
            actionableHotspots.add(ImmutableActionableHotspot.builder().from(actionableEvent).from(hotspot).build());
        }

        return actionableHotspots;
    }

    @NotNull
    public static Set<ActionableRange> toActionableRanges(@NotNull ActionableEvent actionableEvent,
            @Nullable List<? extends RangeAnnotation> ranges) {
        if (ranges == null) {
            return Sets.newHashSet();
        }

        Set<ActionableRange> actionableRange = Sets.newHashSet();
        for (RangeAnnotation range : ranges) {
            actionableRange.add(ImmutableActionableRange.builder().from(actionableEvent).from(range).build());
        }

        return actionableRange;
    }

    @NotNull
    public static ActionableGene geneAnnotationToActionableGene(@NotNull ActionableEvent actionableEvent,
            @NotNull GeneAnnotation geneAnnotation) {
        return ImmutableActionableGene.builder().from(actionableEvent).from(geneAnnotation).build();
    }

    @NotNull
    public static ActionableFusion toActionableFusion(@NotNull ActionableEvent actionableEvent, @NotNull FusionPair fusion) {
        return ImmutableActionableFusion.builder().from(actionableEvent).from(fusion).build();
    }

    @NotNull
    public static ActionableCharacteristic toActionableCharacteristic(@NotNull ActionableEvent actionableEvent,
            @NotNull TumorCharacteristic characteristic) {
        return ImmutableActionableCharacteristic.builder().from(actionableEvent).from(characteristic).build();
    }

    @NotNull
    public static ActionableHLA toActionableHLa(@NotNull ActionableEvent actionableEvent, @NotNull ImmunoHLA hla) {
        return ImmutableActionableHLA.builder().from(actionableEvent).from(hla).build();
    }
}