package com.hartwig.serve.sources.ckb.blacklist;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CkbBlacklistStudyEntry {

    @NotNull
    public abstract CkbBlacklistReason ckbBlacklistReason();

    @NotNull
    public abstract String nctId();

    @Nullable
    public abstract String therapy();

    @Nullable
    public abstract String cancerType();

    @Nullable
    public abstract String molecularProfile();
}
