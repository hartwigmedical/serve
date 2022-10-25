package com.hartwig.serve.datamodel.gene;

import com.hartwig.serve.datamodel.KnownEvent;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class KnownCopyNumber implements KnownEvent {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract CopyNumberType type();
}
