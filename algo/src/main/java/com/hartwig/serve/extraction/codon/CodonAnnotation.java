package com.hartwig.serve.extraction.codon;

import com.hartwig.serve.datamodel.molecular.range.RangeAnnotation;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CodonAnnotation implements RangeAnnotation {

    @NotNull
    public abstract String inputTranscript();

    public abstract int inputCodonRank();

}
