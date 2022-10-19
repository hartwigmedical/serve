package com.hartwig.serve.extraction.util;

import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.common.gene.GeneData;
import com.hartwig.serve.common.gene.TranscriptData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EnsemblFunctions {

    private EnsemblFunctions() {
    }

    @Nullable
    public static HmfTranscriptRegion findCanonicalTranscript(@NotNull EnsemblDataCache ensemblDataCache, @NotNull String gene) {
        GeneData geneData = ensemblDataCache.getGeneDataByName(gene);
        if (geneData == null) {
            return null;
        }

        TranscriptData transcriptData = ensemblDataCache.getCanonicalTranscriptData(geneData.GeneId);
        if (transcriptData == null) {
            return null;
        }

        return HmfTranscriptRegionUtils.fromTranscript(geneData, transcriptData);
    }
}
