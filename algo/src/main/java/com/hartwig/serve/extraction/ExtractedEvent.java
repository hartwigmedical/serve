package com.hartwig.serve.extraction;

import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.common.classification.EventType;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ExtractedEvent {

    // TODO actually maybe can make this a combination of EventInterpretation and
    //  EventExtractorOutput?
    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String event();

    @NotNull
    public abstract Variant variant();

    @NotNull
    public abstract EventType eventType();

    @NotNull
    public abstract EventExtractorOutput eventExtractorOutput();
}
