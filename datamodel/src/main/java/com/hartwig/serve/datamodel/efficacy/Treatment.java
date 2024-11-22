package com.hartwig.serve.datamodel.efficacy;

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
@JsonSerialize(as = ImmutableTreatment.class)
@JsonDeserialize(as = ImmutableTreatment.class)
public abstract class Treatment implements Comparable<Treatment> {

    private static final Comparator<Treatment> COMPARATOR = new TreatmentComparator();

    @NotNull
    public abstract String name();

    @NotNull
    public abstract Set<String> treatmentApproachesDrugClass();

    @NotNull
    public abstract Set<String> treatmentApproachesTherapy();

    @Override
    public int compareTo(Treatment other) {
        return COMPARATOR.compare(this, other);
    }
}
