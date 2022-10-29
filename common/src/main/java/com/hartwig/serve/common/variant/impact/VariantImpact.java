package com.hartwig.serve.common.variant.impact;

import com.hartwig.serve.common.variant.CodingEffect;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class VariantImpact {

    @NotNull
    public abstract String canonicalGeneName();

    @NotNull
    public abstract String canonicalEffect();

    @NotNull
    public abstract String canonicalTranscript();

    @NotNull
    public abstract CodingEffect canonicalCodingEffect();

    @NotNull
    public abstract String canonicalHgvsCoding();

    @NotNull
    public abstract String canonicalHgvsProtein();

    public abstract boolean canonicalSpliceRegion();

    @NotNull
    public abstract String otherReportableEffects();

    @NotNull
    public abstract CodingEffect worstCodingEffect();

    public abstract int genesAffected();

}
