package com.hartwig.serve.datamodel.range;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.KnownEvent;
import com.hartwig.serve.datamodel.common.GeneAlteration;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
@JsonSerialize(as = ImmutableKnownCodon.class)
@JsonDeserialize(as = ImmutableKnownCodon.class)
public abstract class KnownCodon implements RangeAnnotation, GeneAlteration, KnownEvent {

    @NotNull
    public abstract String inputTranscript();

    public abstract int inputCodonRank();

}
