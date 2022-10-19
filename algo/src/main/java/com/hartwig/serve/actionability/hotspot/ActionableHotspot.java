package com.hartwig.serve.actionability.hotspot;

import com.hartwig.serve.actionability.ActionableEvent;
import com.hartwig.serve.common.variant.hotspot.VariantHotspot;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class ActionableHotspot implements ActionableEvent, VariantHotspot {

}


