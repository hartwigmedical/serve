package com.hartwig.serve.sources.ckb.blacklist;

import com.hartwig.serve.datamodel.EvidenceLevel;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CkbBlacklistEvidenceEntry {

    @NotNull
    public abstract CkbBlacklistEvidenceType type();

    @Nullable
    public abstract String therapy();

    @Nullable
    public abstract String cancerType();

    @Nullable
    public abstract String gene();

    @Nullable
    public abstract String event();

    @Nullable
    public abstract EvidenceLevel level();
}