package com.hartwig.serve.sources.hartwig.trial;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class HartwigTrialEntry {

    @NotNull
    public abstract String nctId();

    @NotNull
    public abstract String title();

    @NotNull
    public abstract String acronym();
    
    @NotNull
    public abstract String country();
    
    @Nullable
    public abstract String genderCriterium();

    @NotNull
    public abstract String cancerType();

    @NotNull
    public abstract String molecularCriteriumType();

    @NotNull
    public abstract String molecularCriterium();
    
    @NotNull
    public abstract String url();
}