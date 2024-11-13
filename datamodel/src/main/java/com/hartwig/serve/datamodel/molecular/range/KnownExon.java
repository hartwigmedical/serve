package com.hartwig.serve.datamodel.molecular.range;

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
@JsonSerialize(as = ImmutableKnownExon.class)
@JsonDeserialize(as = ImmutableKnownExon.class)
public abstract class KnownExon implements RangeAnnotation, GeneAlteration, KnownEvent, Comparable<KnownExon> {
    private static final Comparator<KnownExon> COMPARATOR = new KnownExonComparator();

    @NotNull
    public abstract String inputTranscript();

    public abstract int inputExonRank();

    @Override
    public int compareTo(KnownExon other) {
        return COMPARATOR.compare(this, other);
    }
}
