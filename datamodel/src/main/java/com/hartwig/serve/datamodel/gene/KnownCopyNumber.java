package com.hartwig.serve.datamodel.gene;

import java.util.Comparator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.KnownEvent;
import com.hartwig.serve.datamodel.common.GeneAlteration;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableKnownCopyNumber.class)
@JsonDeserialize(as = ImmutableKnownCopyNumber.class)
public abstract class KnownCopyNumber implements GeneAnnotation, GeneAlteration, KnownEvent, Comparable<KnownCopyNumber> {

    private static final Comparator<KnownCopyNumber> COMPARATOR = new KnownCopyNumberComparator();

    @Override
    public int compareTo(KnownCopyNumber other) {
        return COMPARATOR.compare(this, other);
    }
}
