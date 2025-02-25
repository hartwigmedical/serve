package com.hartwig.serve.datamodel.molecular.hotspot;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableActionableHotspotSet.class)
@JsonDeserialize(as = ImmutableActionableHotspotSet.class)
public abstract class ActionableHotspotSet implements Comparable<ActionableHotspotSet> {

    @NotNull
    public abstract Set<ActionableHotspot> hotspots();

    @Override
    public int compareTo(ActionableHotspotSet other) {
        return CompareFunctions.compareSetOfComparable(this.hotspots(), other.hotspots());
    }
}
