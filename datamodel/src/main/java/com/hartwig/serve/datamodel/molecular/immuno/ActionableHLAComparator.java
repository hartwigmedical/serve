package com.hartwig.serve.datamodel.molecular.immuno;

import java.util.Comparator;

import com.hartwig.serve.datamodel.molecular.ActionableEvent;
import com.hartwig.serve.datamodel.molecular.ActionableEventComparator;

import org.jetbrains.annotations.NotNull;

public class ActionableHLAComparator implements Comparator<ActionableHLA> {

    @NotNull
    private final Comparator<ImmunoAnnotation> immunoAnnotationComparator = new ImmunoAnnotationComparator();
    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableHLA hla1, @NotNull ActionableHLA hla2) {
        int immunoAnnotationCompare = immunoAnnotationComparator.compare(hla1, hla2);
        if (immunoAnnotationCompare != 0) {
            return immunoAnnotationCompare;
        }

        return actionableEventComparator.compare(hla1, hla2);
    }
}