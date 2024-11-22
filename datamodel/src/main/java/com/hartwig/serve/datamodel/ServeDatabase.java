package com.hartwig.serve.datamodel;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableServeDatabase.class)
@JsonDeserialize(as = ImmutableServeDatabase.class)
public abstract class ServeDatabase {

    @NotNull
    public abstract String version();

    @NotNull
    public abstract Map<RefGenome, ServeRecord> records();
}
