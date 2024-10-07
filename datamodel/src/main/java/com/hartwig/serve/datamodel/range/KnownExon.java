package com.hartwig.serve.datamodel.range;

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
@JsonSerialize(as = ImmutableKnownExon.class)
@JsonDeserialize(as = ImmutableKnownExon.class)
public abstract class KnownExon implements RangeAnnotation, GeneAlteration, KnownEvent {

    @NotNull
    public abstract String inputTranscript();

    public abstract int inputExonRank();

}
