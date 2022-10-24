package com.hartwig.serve.extraction.exon;

import com.hartwig.serve.extraction.KnownEvent;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class KnownExon implements KnownEvent {

    @NotNull
    public abstract ExonAnnotation annotation();
}
