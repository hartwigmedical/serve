package com.hartwig.serve.datamodel.molecular.characteristic;

import java.util.Comparator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.ActionableEvent;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableActionableCharacteristic.class)
@JsonDeserialize(as = ImmutableActionableCharacteristic.class)
public abstract class ActionableCharacteristic implements TumorCharacteristic, ActionableEvent, Comparable<ActionableCharacteristic> {

    private static final Comparator<ActionableCharacteristic> COMPARATOR = new ActionableCharacteristicComparator();

    @Override
    public int compareTo(ActionableCharacteristic other) {
        return COMPARATOR.compare(this, other);
    }

}