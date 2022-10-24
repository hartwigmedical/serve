package com.hartwig.serve.datamodel.cancertype;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CancerType {

    @NotNull
    public abstract String name();

    @NotNull
    public abstract String doid();
}
