package com.hartwig.serve.datamodel.molecular.hotspot;

import java.util.Comparator;

import com.hartwig.serve.datamodel.molecular.ActionableEvent;
import com.hartwig.serve.datamodel.molecular.ActionableEventComparator;
import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class ActionableHotspotComparator implements Comparator<ActionableHotspot> {

    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableHotspot hotspot1, @NotNull ActionableHotspot hotspot2) {
        int variantCompare = CompareFunctions.compareSetOfComparable(hotspot1.variants(), hotspot2.variants());
        if (variantCompare != 0) {
            return variantCompare;
        }

        return actionableEventComparator.compare(hotspot1, hotspot2);
    }
}
