package com.hartwig.serve.ckb.json.common;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class MolecularProfileInfo {

    public abstract int id();

    @NotNull
    public abstract String profileName();

    public abstract List<TreatmentApproachInfo> treatmentApproaches();
}
