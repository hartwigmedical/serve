package com.hartwig.serve.sources.hartwig.trial;

import com.hartwig.serve.datamodel.trial.GenderCriterium;

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

    @Nullable
    public abstract String acronym();
    
    @NotNull
    public abstract String country();
    
    @Nullable
    public abstract GenderCriterium genderCriterium();

    @NotNull
    public abstract String cancerType();

    @NotNull
    public abstract String cancerTypeDoid();

    @NotNull
    public abstract String molecularCriterium();
    
    @NotNull
    public abstract String url();
}