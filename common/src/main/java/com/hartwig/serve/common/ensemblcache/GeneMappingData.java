package com.hartwig.serve.common.ensemblcache;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class GeneMappingData {

    @NotNull
    public abstract String geneId();

    @NotNull
    public abstract String geneNameNew();

    @NotNull
    public abstract String geneNameOld();

}
