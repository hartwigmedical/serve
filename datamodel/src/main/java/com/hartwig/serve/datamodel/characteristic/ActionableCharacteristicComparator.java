package com.hartwig.serve.datamodel.characteristic;

import java.util.Comparator;

import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.ActionableEventComparator;

import org.jetbrains.annotations.NotNull;

class ActionableCharacteristicComparator implements Comparator<ActionableCharacteristic> {

    @NotNull
    private final Comparator<TumorCharacteristic> tumorCharacteristicComparator = new TumorCharacteristicComparator();
    @NotNull
    private final Comparator<ActionableEvent> actionableEventComparator = new ActionableEventComparator();

    @Override
    public int compare(@NotNull ActionableCharacteristic characteristic1, @NotNull ActionableCharacteristic characteristic2) {
        int characteristicCompare = tumorCharacteristicComparator.compare(characteristic1, characteristic2);
        if (characteristicCompare != 0) {
            return characteristicCompare;
        }

        return actionableEventComparator.compare(characteristic1, characteristic2);
    }
}
