package com.hartwig.serve.datamodel.hotspot;

import com.hartwig.serve.datamodel.KnownEvent;
import com.hartwig.serve.datamodel.common.GeneAlteration;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class KnownHotspot implements VariantHotspot, GeneAlteration, KnownEvent {

    @Nullable
    public abstract String inputTranscript();

    @NotNull
    public abstract String inputProteinAnnotation();

}
