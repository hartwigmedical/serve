package com.hartwig.serve.datamodel.common;

import java.util.Comparator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableCancerType.class)
@JsonDeserialize(as = ImmutableCancerType.class)
public abstract class CancerType implements Comparable<CancerType> {

    private static final Comparator<CancerType> COMPARATOR = new CancerTypeComparator();

    @NotNull
    public abstract String name();

    @NotNull
    public abstract String doid();

    @Override
    public int compareTo(CancerType other) {
        return COMPARATOR.compare(this, other);
    }
}
