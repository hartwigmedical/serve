package com.hartwig.serve.vicc.datamodel.civic;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CivicLastModified {

    @NotNull
    public abstract String timestamp();

    @NotNull
    public abstract CivicUser user();

}
