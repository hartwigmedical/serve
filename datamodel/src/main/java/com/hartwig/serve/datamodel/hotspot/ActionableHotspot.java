package com.hartwig.serve.datamodel.hotspot;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.ActionableEvent;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
@JsonSerialize(as = ImmutableActionableHotspot.class)
@JsonDeserialize(as = ImmutableActionableHotspot.class)
public abstract class ActionableHotspot implements VariantHotspot, ActionableEvent {

}


