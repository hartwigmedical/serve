package com.hartwig.serve.datamodel.fusion;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.KnownEvent;
import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableKnownFusion.class)
@JsonDeserialize(as = ImmutableKnownFusion.class)
public abstract class KnownFusion implements FusionPair, KnownEvent {

    @NotNull
    public abstract ProteinEffect proteinEffect();

    @Nullable
    public abstract Boolean associatedWithDrugResistance();
}
