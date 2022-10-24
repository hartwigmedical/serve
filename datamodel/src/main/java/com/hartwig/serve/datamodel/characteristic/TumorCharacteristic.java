package com.hartwig.serve.datamodel.characteristic;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class TumorCharacteristic {

    // TODO Make an interface, and potentially add an Impl class.

    @NotNull
    public abstract TumorCharacteristicAnnotation name();

    @Nullable
    public abstract TumorCharacteristicsComparator comparator();

    @Nullable
    public abstract Double cutoff();
}
