package com.hartwig.serve.sources.hartwig.gene;

import com.hartwig.serve.datamodel.molecular.common.GeneRole;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class HartwigGeneEntry {

    @NotNull
    public abstract String gene();
    
    @NotNull
    public abstract GeneRole geneRole();
}
