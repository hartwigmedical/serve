package com.hartwig.serve.sources.ckb.filter;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CkbFilterEntry {

    @NotNull
    public abstract CkbFilterType type();

    @NotNull
    public abstract String value();

}
