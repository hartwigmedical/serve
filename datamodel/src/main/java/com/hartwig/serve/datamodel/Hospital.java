package com.hartwig.serve.datamodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableHospital.class)
@JsonDeserialize(as = ImmutableHospital.class)
public abstract class Hospital {

    @NotNull
    public abstract String name();

    public abstract boolean isChildrensHospital();
}
