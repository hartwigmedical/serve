package com.hartwig.serve.ckb.datamodel.treatmentapproaches;

import com.hartwig.serve.ckb.datamodel.drug.DrugClass;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class DrugClassTreatmentApproach extends RelevantTreatmentApproaches{

    @NotNull
    public abstract DrugClass drugClass();
}
