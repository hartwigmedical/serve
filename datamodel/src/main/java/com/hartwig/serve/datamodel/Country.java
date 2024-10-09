package com.hartwig.serve.datamodel;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableCountry.class)
@JsonDeserialize(as = ImmutableCountry.class)
public abstract class Country {

    @NotNull
    public abstract String countryName();

    @NotNull
    public abstract Map<String, Set<String>> hospitalsPerCity();
}