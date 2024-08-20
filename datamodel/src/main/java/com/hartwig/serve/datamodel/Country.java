package com.hartwig.serve.datamodel;

import java.util.Map;
import java.util.Set;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class Country {

    @NotNull
    public abstract String countryName();

    @NotNull
    public abstract Map<String, Set<String>> hospitalsPerCity();
}