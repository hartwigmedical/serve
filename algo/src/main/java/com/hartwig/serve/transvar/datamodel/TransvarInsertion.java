package com.hartwig.serve.transvar.datamodel;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class TransvarInsertion implements TransvarAnnotation {

    @NotNull
    public abstract String insertedBases();

    public abstract long leftAlignedGDNAPosition();
}
