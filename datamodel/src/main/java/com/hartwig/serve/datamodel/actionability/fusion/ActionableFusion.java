package com.hartwig.serve.datamodel.actionability.fusion;

import com.hartwig.serve.datamodel.actionability.ActionableEvent;
import com.hartwig.serve.datamodel.fusion.FusionPair;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class ActionableFusion implements FusionPair, ActionableEvent {

}