package com.hartwig.serve.common.ensemblcache;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ExonData {

    public abstract int start();

    public abstract int end();

    public abstract int rank();

    public abstract int phaseStart();

    public abstract int phaseEnd();
}
