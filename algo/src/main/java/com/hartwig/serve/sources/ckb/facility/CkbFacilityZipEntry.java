package com.hartwig.serve.sources.ckb.facility;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CkbFacilityZipEntry {

    @NotNull
    public abstract String city();

    @NotNull
    public abstract String zip();

    @NotNull
    public abstract String curatedFacilityName();
}
