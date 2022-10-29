package com.hartwig.serve.common.variant.impact;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class VariantTranscriptImpact {

    @NotNull
    public abstract String geneId();

    @NotNull
    public abstract String geneName();

    @NotNull
    public abstract String transcript();

    @NotNull
    public abstract String effects();

    public abstract boolean spliceRegion();

    @NotNull
    public abstract String hgvsCoding();

    @Nullable
    public abstract String hgvsProtein();

}
