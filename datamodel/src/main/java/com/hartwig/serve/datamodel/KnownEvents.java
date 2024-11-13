package com.hartwig.serve.datamodel;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hartwig.serve.datamodel.molecular.fusion.KnownFusion;
import com.hartwig.serve.datamodel.molecular.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.molecular.gene.KnownGene;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.molecular.range.KnownCodon;
import com.hartwig.serve.datamodel.molecular.range.KnownExon;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class },
             jdkOnly = true)
@JsonSerialize(as = ImmutableKnownEvents.class)
@JsonDeserialize(as = ImmutableKnownEvents.class)
public abstract class KnownEvents {

    @NotNull
    public abstract Set<KnownHotspot> hotspots();

    @NotNull
    public abstract Set<KnownCodon> codons();

    @NotNull
    public abstract Set<KnownExon> exons();

    @NotNull
    public abstract Set<KnownGene> genes();

    @NotNull
    public abstract Set<KnownCopyNumber> copyNumbers();

    @NotNull
    public abstract Set<KnownFusion> fusions();

}
