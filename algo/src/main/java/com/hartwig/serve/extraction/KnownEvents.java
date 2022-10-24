package com.hartwig.serve.extraction;

import java.util.Set;

import com.hartwig.serve.extraction.codon.KnownCodon;
import com.hartwig.serve.extraction.copynumber.KnownCopyNumber;
import com.hartwig.serve.extraction.exon.KnownExon;
import com.hartwig.serve.extraction.fusion.KnownFusionPair;
import com.hartwig.serve.extraction.hotspot.KnownHotspot;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class KnownEvents {

    @NotNull
    public abstract Set<KnownHotspot> knownHotspots();

    @NotNull
    public abstract Set<KnownCodon> knownCodons();

    @NotNull
    public abstract Set<KnownExon> knownExons();

    @NotNull
    public abstract Set<KnownCopyNumber> knownCopyNumbers();

    @NotNull
    public abstract Set<KnownFusionPair> knownFusionPairs();

}
