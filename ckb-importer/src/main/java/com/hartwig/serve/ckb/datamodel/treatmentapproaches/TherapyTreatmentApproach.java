package com.hartwig.serve.ckb.datamodel.treatmentapproaches;

import com.hartwig.serve.ckb.datamodel.therapy.Therapy;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class TherapyTreatmentApproach implements TreatmentApproachIntervation{

    @NotNull
    public abstract Therapy therapy();
}