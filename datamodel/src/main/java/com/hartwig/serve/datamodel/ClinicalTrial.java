package com.hartwig.serve.datamodel;

import java.util.List;
import java.util.Map;
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

    @Nullable
    public abstract String studyAcronym();

    @NotNull
    public abstract Map<String, List<String>> locationsOfStudy();

    @NotNull
    public abstract Set<String> therapyNames();

    @Nullable
    public abstract String gender();
}