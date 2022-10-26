package com.hartwig.serve.sources.ckb.treatmentapproach;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class RelevantTreatmentApproachCurationEntry {

    @NotNull
    public abstract RelevantTreatmentApproachCurationType curationType();

    @NotNull
    public abstract RelevantTreatmentApproachCurationEntryKey curationKey();

    @Nullable
    public abstract String curatedTreatmentApproach();
}