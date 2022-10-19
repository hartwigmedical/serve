package com.hartwig.serve.extraction.codon;

import com.hartwig.serve.datamodel.range.RangeAnnotation;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class CodonAnnotation implements RangeAnnotation {

}
