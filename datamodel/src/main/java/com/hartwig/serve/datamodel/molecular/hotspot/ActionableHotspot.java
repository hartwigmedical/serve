package com.hartwig.serve.datamodel.molecular.hotspot;

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
@JsonSerialize(as = ImmutableActionableHotspot.class)
@JsonDeserialize(as = ImmutableActionableHotspot.class)
public abstract class ActionableHotspot implements VariantHotspot, ActionableEvent, Comparable<ActionableHotspot> {

    private static final Comparator<ActionableHotspot> COMPARATOR = new ActionableHotspotComparator();

    @Override
    public int compareTo(ActionableHotspot other) {
        return COMPARATOR.compare(this, other);
    }
}


