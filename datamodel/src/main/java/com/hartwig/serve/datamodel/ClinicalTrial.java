package com.hartwig.serve.datamodel;

import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ClinicalTrial implements Intervention {

    @NotNull
    public abstract String nctId();

    @NotNull
    public abstract String title();

    @Nullable
    public abstract String acronym();

    @NotNull
    public abstract Set<Country> countries();

    @NotNull
    public abstract Set<String> therapyNames();

    @Nullable
    public abstract String genderCriterium();
}