package com.hartwig.serve.datamodel.molecular.gene;

import java.util.Comparator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.molecular.ActionableEvent;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableActionableGene.class)
@JsonDeserialize(as = ImmutableActionableGene.class)
public abstract class ActionableGene implements GeneAnnotation, ActionableEvent, Comparable<ActionableGene> {

    private static final Comparator<ActionableGene> COMPARATOR = new ActionableGeneComparator();

    @Override
    public int compareTo(ActionableGene other) {
        return COMPARATOR.compare(this, other);
    }
}
