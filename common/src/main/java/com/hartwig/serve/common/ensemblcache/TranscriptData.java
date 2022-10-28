package com.hartwig.serve.common.ensemblcache;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class TranscriptData {

    public abstract int transcriptId();

    @NotNull
    public abstract String transcriptName();

    @NotNull
    public abstract String geneId();

    public abstract boolean isCanonical();

    public abstract byte strand();

    public abstract int transcriptStart();

    public abstract int transcriptEnd();

    @Nullable
    public abstract Integer codingStart();

    @Nullable
    public abstract Integer codingEnd();

    @NotNull
    public abstract String bioType();

    @NotNull
    public abstract List<ExonData> exons();
}
