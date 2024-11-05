package com.hartwig.serve.sources.ckb.blacklist;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CkbMolecularProfileFilterEntry {

    @NotNull
    public abstract CkbMolecularProfileFilterType type();

    @NotNull
    public abstract String value();

}
