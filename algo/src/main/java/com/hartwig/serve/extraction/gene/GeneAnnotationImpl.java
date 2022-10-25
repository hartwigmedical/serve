package com.hartwig.serve.extraction.gene;

import com.hartwig.serve.datamodel.gene.GeneAnnotation;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class GeneAnnotationImpl implements GeneAnnotation {

}
