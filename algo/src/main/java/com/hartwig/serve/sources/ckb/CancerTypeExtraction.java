package com.hartwig.serve.sources.ckb;

import java.util.Set;

import com.hartwig.serve.datamodel.CancerType;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
abstract class CancerTypeExtraction {

    @NotNull
    public abstract CancerType applicableCancerType();

    @NotNull
    public abstract Set<CancerType> blacklistedCancerTypes();
}
