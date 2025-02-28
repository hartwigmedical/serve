package com.hartwig.serve.datamodel.molecular.hotspot;

import java.util.Comparator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableVariantAnnotation.class)
@JsonDeserialize(as = ImmutableVariantAnnotation.class)
public abstract class VariantAnnotation implements VariantHotspot, Comparable<VariantAnnotation> {

    private static final Comparator<VariantHotspot> COMPARATOR = new VariantHotspotComparator();

    @Override
    public int compareTo(VariantAnnotation other) {
        return COMPARATOR.compare(this, other);
    }

}
