package com.hartwig.serve.actionability.characteristic;

import com.hartwig.serve.actionability.ActionableEvent;
import com.hartwig.serve.extraction.characteristic.TumorCharacteristicAnnotation;
import com.hartwig.serve.extraction.characteristic.TumorCharacteristicsComparator;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class ActionableCharacteristic implements ActionableEvent {

    @NotNull
    public abstract TumorCharacteristicAnnotation name();

    @Nullable
    public abstract TumorCharacteristicsComparator comparator();

    @Nullable
    public abstract Double cutoff();
}