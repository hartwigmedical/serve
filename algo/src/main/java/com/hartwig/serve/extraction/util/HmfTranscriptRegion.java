package com.hartwig.serve.extraction.util;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.common.ensemblcache.Strand;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class HmfTranscriptRegion implements TranscriptRegion {

    @NotNull
    public abstract String geneId();

    public abstract int geneStart();

    public abstract int geneEnd();

    @Nullable
    public abstract Integer codingStart();

    @Nullable
    public abstract Integer codingEnd();

    @NotNull
    public abstract Strand strand();

    @NotNull
    public abstract List<Integer> entrezId();

    @NotNull
    public abstract List<HmfExonRegion> exons();

    @Nullable
    public HmfExonRegion exonByIndex(int index) {
        int effectiveIndex = index - 1;
        List<HmfExonRegion> strandSortedExome = strandSortedExome();

        if (effectiveIndex >= 0 && effectiveIndex < strandSortedExome.size()) {
            return strandSortedExome.get(effectiveIndex);
        }

        return null;
    }

    @NotNull
    public List<HmfExonRegion> strandSortedExome() {
        return strand() == Strand.FORWARD ? exons() : Lists.reverse(exons());
    }
}
