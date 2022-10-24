package com.hartwig.serve.transvar.datamodel;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class TransvarSnvMnv implements TransvarAnnotation {

    @NotNull
    public abstract String gdnaRef();

    @NotNull
    public abstract String gdnaAlt();

    @NotNull
    public abstract String referenceCodon();

    @NotNull
    public abstract List<String> candidateCodons();
}
