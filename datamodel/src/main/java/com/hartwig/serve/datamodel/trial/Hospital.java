package com.hartwig.serve.datamodel.trial;

import java.util.Comparator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableHospital.class)
@JsonDeserialize(as = ImmutableHospital.class)
public abstract class Hospital implements Comparable<Hospital> {

    private static final Comparator<Hospital> COMPARATOR = new HospitalComparator();

    @NotNull
    public abstract String name();

    @Nullable
    public abstract Boolean isChildrensHospital();

    @Override
    public int compareTo(Hospital other) {
        return COMPARATOR.compare(this, other);
    }
}
