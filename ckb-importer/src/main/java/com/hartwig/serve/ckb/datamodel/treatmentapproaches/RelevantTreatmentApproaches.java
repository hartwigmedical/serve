package com.hartwig.serve.ckb.datamodel.treatmentapproaches;

import java.time.LocalDate;
import java.util.List;

import com.hartwig.serve.ckb.datamodel.drug.DrugClass;
import com.hartwig.serve.ckb.datamodel.reference.Reference;
import com.hartwig.serve.ckb.datamodel.therapy.Therapy;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class RelevantTreatmentApproaches {

    public abstract int id();

    @Nullable
    public abstract DrugClass drugClass();

    @Nullable
    public abstract Therapy therapy();

    @NotNull
    public abstract List<Reference> references();

    @NotNull
    public abstract LocalDate createDate();

    @Nullable
    public abstract LocalDate updateDate();
}
