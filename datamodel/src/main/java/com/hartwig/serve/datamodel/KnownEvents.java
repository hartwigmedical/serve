package com.hartwig.serve.datamodel;

import java.util.Set;

import com.hartwig.serve.datamodel.fusion.KnownFusionPair;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class KnownEvents {

    @NotNull
    public abstract Set<KnownHotspot> hotspots();

    @NotNull
    public abstract Set<KnownCodon> codons();

    @NotNull
    public abstract Set<KnownExon> exons();

    @NotNull
    public abstract Set<KnownCopyNumber> copyNumbers();

    @NotNull
    public abstract Set<KnownFusionPair> fusionPairs();

}
