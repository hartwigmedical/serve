package com.hartwig.serve.datamodel.molecular.gene;

import java.util.Comparator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.KnownEvent;
import com.hartwig.serve.datamodel.molecular.common.GeneRole;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableKnownGene.class)
@JsonDeserialize(as = ImmutableKnownGene.class)

public abstract class KnownGene implements KnownEvent, Comparable<KnownGene> {

    private static final Comparator<KnownGene> COMPARATOR = new KnownGeneComparator();

    @NotNull
    public abstract String gene();

    @NotNull
    public abstract GeneRole geneRole();

    @Override
    public int compareTo(KnownGene other) {
        return COMPARATOR.compare(this, other);
    }
}
