package com.hartwig.serve.common.ensemblcache;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class GeneData {

    @NotNull
    public abstract String geneId();

    @NotNull
    public abstract String geneName();

    @NotNull
    public abstract String chromosome();

    public abstract byte strand();

    public abstract int geneStart();

    public abstract int geneEnd();

    @NotNull
    public abstract String karyotypeBand();
}
