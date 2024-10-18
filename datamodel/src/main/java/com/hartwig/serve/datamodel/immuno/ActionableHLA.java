package com.hartwig.serve.datamodel.immuno;

import java.util.Comparator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.MolecularEvent;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableActionableHLA.class)
@JsonDeserialize(as = ImmutableActionableHLA.class)
public abstract class ActionableHLA implements ImmunoAnnotation, MolecularEvent, Comparable<ActionableHLA> {

    private static final Comparator<ActionableHLA> COMPARATOR = new ActionableHLAComparator();

    @Override
    public int compareTo(ActionableHLA other) {
        return COMPARATOR.compare(this, other);
    }
}
