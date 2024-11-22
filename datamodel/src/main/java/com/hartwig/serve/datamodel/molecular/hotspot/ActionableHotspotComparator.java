package com.hartwig.serve.datamodel.molecular.hotspot;

import java.util.Comparator;

import com.hartwig.serve.datamodel.molecular.ActionableEvent;
import com.hartwig.serve.datamodel.molecular.ActionableEventComparator;

import org.jetbrains.annotations.NotNull;

public class ActionableHotspotComparator implements Comparator<ActionableHotspot> {

    @NotNull
    private final Comparator<VariantHotspot> variantHotspotComparator = new VariantHotspotComparator();
    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableHotspot hotspot1, @NotNull ActionableHotspot hotspot2) {
        int hotspotCompare = variantHotspotComparator.compare(hotspot1, hotspot2);
        if (hotspotCompare != 0) {
            return hotspotCompare;
        }

        return actionableEventComparator.compare(hotspot1, hotspot2);
    }
}
