package com.hartwig.serve.actionability.fusion;

import com.hartwig.serve.actionability.ActionableEvent;
import com.hartwig.serve.datamodel.fusion.FusionPair;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class ActionableFusion implements FusionPair, ActionableEvent {

}