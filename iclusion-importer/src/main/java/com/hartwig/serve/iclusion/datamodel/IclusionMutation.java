package com.hartwig.serve.iclusion.datamodel;

import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.iclusion.classification.IclusionEventTypeExtractor;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class IclusionMutation {

    @NotNull
    @Value.Derived
    public EventType type() {
        return IclusionEventTypeExtractor.classify(this);
    }

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract String name();

    public abstract boolean negation();
}
