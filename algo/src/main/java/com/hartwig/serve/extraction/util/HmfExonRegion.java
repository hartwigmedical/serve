package com.hartwig.serve.extraction.util;

import com.hartwig.serve.datamodel.molecular.common.GenomeRegion;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class HmfExonRegion implements GenomeRegion {

    public abstract int exonRank();
}
