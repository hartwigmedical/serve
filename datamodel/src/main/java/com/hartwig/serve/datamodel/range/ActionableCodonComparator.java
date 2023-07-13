package com.hartwig.serve.datamodel.range;

import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.ActionableEventComparator;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ActionableCodonComparator implements Comparator<ActionableCodon> {

    @NotNull
    private final Comparator<RangeAnnotation> rangeAnnotationComparator = new RangeAnnotationComparator();
    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableCodon codon1, @NotNull ActionableCodon codon2) {
        int rangeCompare = rangeAnnotationComparator.compare(codon1, codon2);
        if (rangeCompare != 0) {
            return rangeCompare;
        }

        return actionableEventComparator.compare(codon1, codon2);
    }
}
