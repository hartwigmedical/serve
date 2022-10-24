package com.hartwig.serve.extraction.hotspot;

import com.hartwig.serve.datamodel.hotspot.VariantHotspot;
import com.hartwig.serve.extraction.KnownEvent;

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
