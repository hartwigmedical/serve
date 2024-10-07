package com.hartwig.serve.datamodel.gene;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.KnownEvent;
import com.hartwig.serve.datamodel.common.GeneRole;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableKnownGene.class)
@JsonDeserialize(as = ImmutableKnownGene.class)
public abstract class KnownGene implements KnownEvent {

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract GeneRole geneRole();

}
