package com.hartwig.serve.datamodel.molecular.characteristic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TumorCharacteristic {

    @NotNull
    TumorCharacteristicType type();

    @Nullable
    TumorCharacteristicCutoffType cutoffType();

    @Nullable
    Double cutoff();
}
