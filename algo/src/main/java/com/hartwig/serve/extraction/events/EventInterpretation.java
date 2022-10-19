package com.hartwig.serve.extraction.events;

import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class EventInterpretation {

    @NotNull
    public abstract Knowledgebase source();

    @NotNull
    public abstract String sourceEvent();

    @NotNull
    public abstract String interpretedGene();

    @NotNull
    public abstract String interpretedEvent();

    @NotNull
    public abstract EventType interpretedEventType();
}