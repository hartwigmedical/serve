package com.hartwig.serve.datamodel.molecular.hotspot;

import java.util.Comparator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.molecular.KnownEvent;
import com.hartwig.serve.datamodel.molecular.common.GeneAlteration;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableKnownHotspot.class)
@JsonDeserialize(as = ImmutableKnownHotspot.class)
public abstract class KnownHotspot implements VariantHotspot, GeneAlteration, KnownEvent, Comparable<KnownHotspot> {

    private static final Comparator<KnownHotspot> COMPARATOR = new KnownHotspotComparator();

    @Nullable
    public abstract String inputTranscript();

    @NotNull
    public abstract String inputProteinAnnotation();

    @Override
    public int compareTo(KnownHotspot other) {
        return COMPARATOR.compare(this, other);
    }
}
