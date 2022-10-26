package com.hartwig.serve.datamodel.immuno;

import java.util.Comparator;

import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.ActionableEventComparator;

import org.jetbrains.annotations.NotNull;

public class ActionableHLAComparator implements Comparator<ActionableHLA> {

    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableHLA hla1, @NotNull ActionableHLA hla2) {
        int hlaCompare = hla1.hlaAllele().compareTo(hla2.hlaAllele());
        if (hlaCompare != 0) {
            return hlaCompare;
        }

        return actionableEventComparator.compare(hla1, hla2);
    }
}