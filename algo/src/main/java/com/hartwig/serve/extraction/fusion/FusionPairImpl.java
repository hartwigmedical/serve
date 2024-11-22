package com.hartwig.serve.extraction.fusion;

import com.hartwig.serve.datamodel.molecular.fusion.FusionPair;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class FusionPairImpl implements FusionPair {

}
