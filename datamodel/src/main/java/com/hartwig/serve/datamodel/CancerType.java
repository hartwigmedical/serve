package com.hartwig.serve.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
@JsonSerialize(as = ImmutableCancerType.class)
@JsonDeserialize(as = ImmutableCancerType.class)
public abstract class CancerType {

    @NotNull
    public abstract String name();

    @NotNull
    public abstract String doid();
}
