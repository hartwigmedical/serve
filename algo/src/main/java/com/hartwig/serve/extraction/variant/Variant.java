package com.hartwig.serve.extraction.variant;

import com.hartwig.serve.datamodel.molecular.common.GenomePosition;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class Variant implements GenomePosition {

    @NotNull
    public abstract String ref();

    @NotNull
    public abstract String alt();

}
