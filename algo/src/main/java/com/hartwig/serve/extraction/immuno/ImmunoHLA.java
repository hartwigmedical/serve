package com.hartwig.serve.extraction.immuno;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ImmunoHLA {

    @NotNull
    public abstract String immunoHLA();
}
