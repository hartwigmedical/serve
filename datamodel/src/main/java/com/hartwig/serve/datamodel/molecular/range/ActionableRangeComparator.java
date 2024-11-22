package com.hartwig.serve.datamodel.molecular.range;

import java.util.Comparator;

import com.hartwig.serve.datamodel.molecular.ActionableEvent;
import com.hartwig.serve.datamodel.molecular.ActionableEventComparator;

import org.jetbrains.annotations.NotNull;

public class ActionableRangeComparator implements Comparator<ActionableRange> {

    @NotNull
    private final Comparator<RangeAnnotation> rangeAnnotationComparator = new RangeAnnotationComparator();
    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableRange range1, @NotNull ActionableRange range2) {
        int rangeCompare = rangeAnnotationComparator.compare(range1, range2);
        if (rangeCompare != 0) {
            return rangeCompare;
        }

        return actionableEventComparator.compare(range1, range2);
    }
}
