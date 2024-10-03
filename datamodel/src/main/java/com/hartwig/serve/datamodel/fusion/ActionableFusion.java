package com.hartwig.serve.datamodel.fusion;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.ActionableEvent;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
@JsonSerialize(as = ImmutableActionableFusion.class)
@JsonDeserialize(as = ImmutableActionableFusion.class)
public abstract class ActionableFusion implements FusionPair, ActionableEvent {

}