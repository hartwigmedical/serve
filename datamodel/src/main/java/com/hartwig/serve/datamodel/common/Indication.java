package com.hartwig.serve.datamodel.common;

import java.util.Comparator;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableIndication.class)
@JsonDeserialize(as = ImmutableIndication.class)
public abstract class Indication implements Comparable<Indication> {

    private static final Comparator<Indication> COMPARATOR = new IndicationComparator();

    @NotNull
    public abstract CancerType applicableType();

    @NotNull
    public abstract Set<CancerType> excludedSubTypes();

    @Override
    public int compareTo(Indication other) {
        return COMPARATOR.compare(this, other);
    }
}
