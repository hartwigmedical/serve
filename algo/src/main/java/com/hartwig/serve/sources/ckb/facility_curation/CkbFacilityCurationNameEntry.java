package com.hartwig.serve.sources.ckb.facility_curation;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CkbFacilityCurationNameEntry {

    @NotNull
    public abstract String facilityName();

    @NotNull
    public abstract String city();

    @NotNull
    public abstract String curatedFacilityName();
}
