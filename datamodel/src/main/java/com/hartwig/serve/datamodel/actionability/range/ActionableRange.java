package com.hartwig.serve.datamodel.actionability.range;

import com.hartwig.serve.datamodel.actionability.ActionableEvent;
import com.hartwig.serve.datamodel.range.RangeAnnotation;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class ActionableRange implements ActionableEvent, RangeAnnotation {

    @NotNull
    public abstract RangeType rangeType();
}
