package com.hartwig.serve.datamodel.hotspot;

import com.hartwig.serve.datamodel.KnownEvent;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class KnownHotspot implements VariantHotspot, KnownEvent {

    @Nullable
    public abstract String transcript();

    @NotNull
    public abstract String proteinAnnotation();

}
