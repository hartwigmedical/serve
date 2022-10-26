package com.hartwig.serve.sources.ckb.treatmentapproach;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class TreatmentApproachCurationEntry {

    @NotNull
    public abstract TreatmentApproachCurationType curationType();

    @NotNull
    public abstract TreatmentApproachCurationEntryKey curationKey();

    @Nullable
    public abstract String curatedTreatmentApproach();
}