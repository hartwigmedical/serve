package com.hartwig.serve.datamodel.molecular.fusion;

import java.util.Comparator;

import com.hartwig.serve.datamodel.molecular.ActionableEvent;
import com.hartwig.serve.datamodel.molecular.ActionableEventComparator;

import org.jetbrains.annotations.NotNull;

public class ActionableFusionComparator implements Comparator<ActionableFusion> {

    @NotNull
    private final Comparator<FusionPair> fusionPairComparator = new FusionPairComparator();
    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableFusion fusion1, @NotNull ActionableFusion fusion2) {
        int fusionPairCompare = fusionPairComparator.compare(fusion1, fusion2);
        if (fusionPairCompare != 0) {
            return fusionPairCompare;
        }

        return actionableEventComparator.compare(fusion1, fusion2);
    }
}
