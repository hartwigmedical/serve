package com.hartwig.serve.datamodel.range;

import com.hartwig.serve.datamodel.KnownEvent;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class KnownCodon implements KnownEvent {

    @NotNull
    public abstract CodonAnnotation annotation();
}
