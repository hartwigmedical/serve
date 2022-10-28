package com.hartwig.serve.common.ensemblcache;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class TranscriptProteinData {

    public abstract int transcriptId();

    public abstract int translationId();

    public abstract int proteinFeatureId();

    public abstract int seqStart();

    public abstract int seqEnd();

    @NotNull
    public abstract String hitDescription();
}
