package com.hartwig.serve.transvar.datamodel;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class TransvarFrameshift implements TransvarAnnotation {

    public abstract boolean isFrameshiftInsideStartCodon();

}
