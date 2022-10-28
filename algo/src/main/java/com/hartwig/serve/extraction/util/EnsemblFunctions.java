package com.hartwig.serve.extraction.util;

import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.common.ensemblcache.GeneData;
import com.hartwig.serve.common.ensemblcache.TranscriptData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EnsemblFunctions {

    private EnsemblFunctions() {
    }

    @Nullable
    public static HmfTranscriptRegion findCanonicalTranscript(@NotNull EnsemblDataCache ensemblDataCache, @NotNull String geneNameToFind) {
        GeneData gene = ensemblDataCache.findGeneDataByName(geneNameToFind);
        if (gene == null) {
            return null;
        }

        TranscriptData transcript = ensemblDataCache.findCanonicalTranscript(gene.geneId());
        if (transcript == null) {
            return null;
        }

        return HmfTranscriptRegionUtils.fromTranscript(gene, transcript);
    }
}
