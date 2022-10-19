package com.hartwig.serve.datamodel.actionability.hotspot;

import java.util.Comparator;

import com.hartwig.serve.common.variant.hotspot.VariantHotspot;
import com.hartwig.serve.common.variant.hotspot.VariantHotspotComparator;
import com.hartwig.serve.datamodel.actionability.ActionableEvent;
import com.hartwig.serve.datamodel.actionability.ActionableEventComparator;

import org.jetbrains.annotations.NotNull;

class ActionableHotspotComparator implements Comparator<ActionableHotspot> {

    @NotNull
    private final Comparator<VariantHotspot> hotspotComparator = new VariantHotspotComparator();
    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableHotspot hotspot1, @NotNull ActionableHotspot hotspot2) {
        int hotspotCompare = hotspotComparator.compare(hotspot1, hotspot2);
        if (hotspotCompare != 0) {
            return hotspotCompare;
        }

        return actionableEventComparator.compare(hotspot1, hotspot2);
    }
}
