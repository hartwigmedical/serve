package com.hartwig.serve.transvar.datamodel;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class TransvarComplexInsertDelete implements TransvarAnnotation {

    public abstract int deletedBaseCount();

    @NotNull
    public abstract String insertedSequence();

    @NotNull
    public abstract List<String> candidateAlternativeCodons();
}
