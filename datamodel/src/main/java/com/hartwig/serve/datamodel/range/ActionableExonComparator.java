package com.hartwig.serve.datamodel.range;

import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.ActionableEventComparator;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ActionableExonComparator implements Comparator<ActionableExon> {

    @NotNull
    private final Comparator<RangeAnnotation> rangeAnnotationComparator = new RangeAnnotationComparator();
    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableExon exon1, @NotNull ActionableExon exon2) {
        int rangeCompare = rangeAnnotationComparator.compare(exon1, exon2);
        if (rangeCompare != 0) {
            return rangeCompare;
        }

        return actionableEventComparator.compare(exon1, exon2);
    }
}
