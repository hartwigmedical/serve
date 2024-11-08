package com.hartwig.serve.datamodel;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableActionableTrial.class)
@JsonDeserialize(as = ImmutableActionableTrial.class)
public abstract class ActionableTrial {

    @NotNull
    public abstract Knowledgebase source();

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
    public abstract GenderCriterium genderCriterium();

    @NotNull
    public abstract Set<Indication> indications();

    @NotNull
    public abstract Set<MolecularCriterium> molecularCriteria();

    @NotNull
    public abstract Set<String> urls();
}
