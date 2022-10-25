package com.hartwig.serve.extraction.characteristic;

import com.hartwig.serve.datamodel.characteristic.TumorCharacteristic;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class TumorCharacteristicImpl implements TumorCharacteristic {

}
