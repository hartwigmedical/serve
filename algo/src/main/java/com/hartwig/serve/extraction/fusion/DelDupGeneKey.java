package com.hartwig.serve.extraction.fusion;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class DelDupGeneKey {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract ExonicDelDupType exonicDelDupType();
}