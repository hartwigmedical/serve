package com.hartwig.serve.extraction.immuno;

import com.hartwig.serve.datamodel.immuno.ImmunoAnnotation;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ImmunoHLA implements ImmunoAnnotation {

}
