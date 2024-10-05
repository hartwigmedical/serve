package com.hartwig.serve.ckb.json.clinicaltrial;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class JsonLocation {

    @NotNull
    public abstract String nctId();

    @Nullable
    public abstract String facility();

    @Nullable
    public abstract String city();

    @Nullable
    public abstract String country();

    @Nullable
    public abstract String status();

    @Nullable
    public abstract String state();

    @Nullable
    public abstract String zip();

    @NotNull
    public abstract List<JsonContact> contacts();
}
