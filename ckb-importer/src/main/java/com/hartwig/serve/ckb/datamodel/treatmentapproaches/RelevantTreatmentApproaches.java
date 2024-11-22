package com.hartwig.serve.ckb.datamodel.treatmentapproaches;

import java.time.LocalDate;
import java.util.List;

import com.hartwig.serve.ckb.datamodel.reference.Reference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RelevantTreatmentApproaches {

    public abstract int id();

    @NotNull
    public abstract List<Reference> references();

    @NotNull
    public abstract LocalDate createDate();

    @Nullable
    public abstract LocalDate updateDate();
}
