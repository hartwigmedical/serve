package com.hartwig.serve.datamodel;

import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class Treatment implements Intervention {

    @NotNull
    public abstract String name();

    @NotNull
    public abstract Set<String> treatmentApproachesDrugClass();

    @NotNull
    public abstract Set<String> treatmentApproachesTherapy();
}
