package com.hartwig.serve.sources.actin.reader;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ActinEntry {

    @NotNull
    public abstract String trial();

    @Nullable
    public abstract String cohort();

    @NotNull
    public abstract ActinRule rule();

    @Nullable
    public abstract String gene();

    @Nullable
    public abstract String mutation();

    public abstract boolean isUsedAsInclusion();
}
