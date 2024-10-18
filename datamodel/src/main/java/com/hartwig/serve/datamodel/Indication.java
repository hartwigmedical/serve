package com.hartwig.serve.datamodel;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableTreatment.class)
@JsonDeserialize(as = ImmutableTreatment.class)
public abstract class Indication {

    @NotNull
    public abstract CancerType applicableCancerType();

    @NotNull
    public abstract Set<CancerType> blacklistCancerTypes();
}
