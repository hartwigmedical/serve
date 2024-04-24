package com.hartwig.serve.datamodel;

import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ClinicalTrial implements Intervention {

    @NotNull
    public abstract String studyNctId();

    @NotNull
    public abstract String studyTitle();

    @NotNull
    public abstract Set<String> countriesOfStudy();

    @NotNull
    public abstract String therapyName();
}