package com.hartwig.serve.datamodel.trial;

import java.util.Comparator;
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
public abstract class Country implements Comparable<Country> {

    private static final Comparator<Country> COMPARATOR = new CountryComparator();

    @NotNull
    public abstract String name();

    @NotNull
    public abstract Map<String, Set<Hospital>> hospitalsPerCity();

    @Override
    public int compareTo(Country other) {
        return COMPARATOR.compare(this, other);
    }
}