package com.hartwig.serve.datamodel.range;

import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.ActionableEventComparator;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

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
