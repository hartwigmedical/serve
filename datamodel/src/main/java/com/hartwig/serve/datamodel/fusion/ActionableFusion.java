package com.hartwig.serve.datamodel.fusion;

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
@JsonSerialize(as = ImmutableActionableFusion.class)
@JsonDeserialize(as = ImmutableActionableFusion.class)
public abstract class ActionableFusion implements FusionPair, ActionableEvent, Comparable<ActionableFusion> {

    private static final Comparator<ActionableFusion> COMPARATOR = new ActionableFusionComparator();

    @Override
    public int compareTo(ActionableFusion other) {
        return COMPARATOR.compare(this, other);
    }

}