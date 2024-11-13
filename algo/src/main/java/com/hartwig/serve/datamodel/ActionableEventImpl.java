package com.hartwig.serve.datamodel;

import com.hartwig.serve.datamodel.molecular.ActionableEvent;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ActionableEventImpl implements ActionableEvent {

}
