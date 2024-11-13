package com.hartwig.serve.datamodel.molecular.range;

import java.util.Comparator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.ActionableEvent;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableActionableRange.class)
@JsonDeserialize(as = ImmutableActionableRange.class)
public abstract class ActionableRange implements RangeAnnotation, ActionableEvent, Comparable<ActionableRange> {

    private static final Comparator<ActionableRange> COMPARATOR = new ActionableRangeComparator();

    @Override
    public int compareTo(ActionableRange other) {
        return COMPARATOR.compare(this, other);
    }
}