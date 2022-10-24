package com.hartwig.serve.refgenome.liftover;

import com.hartwig.serve.datamodel.common.GenomePosition;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class LiftOverResult implements GenomePosition {

}
