package com.hartwig.serve.common.ensemblcache;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnsemblDataCache {

    @NotNull
    private final Map<String, List<GeneData>> genesPerChromosome;
    @NotNull
    private final Map<String, GeneData> genesPerName;
    @NotNull
    private final Map<String, List<TranscriptData>> transcriptsPerGeneId;

    public EnsemblDataCache(@NotNull final Map<String, List<GeneData>> genesPerChromosome,
            @NotNull final Map<String, List<TranscriptData>> transcriptsPerGeneId) {
        this.genesPerChromosome = genesPerChromosome;
        this.transcriptsPerGeneId = transcriptsPerGeneId;
        this.genesPerName = genesPerChromosome.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(GeneData::geneName, gene -> gene));
    }

    @NotNull
    public Map<String, List<GeneData>> genesPerChromosome() {
        return genesPerChromosome;
    }

    @NotNull
    public Map<String, List<TranscriptData>> transcriptsPerGeneId() {
        return transcriptsPerGeneId;
    }

    @Nullable
    public GeneData findGeneDataByName(@NotNull String geneNameToFind) {
        return genesPerName.get(geneNameToFind);
    }

    @Nullable
    public TranscriptData findCanonicalTranscript(@NotNull String geneId) {
        List<TranscriptData> transcripts = transcriptsPerGeneId.get(geneId);

        return transcripts == null ? null : transcripts.stream()
                .filter(TranscriptData::isCanonical)
                .findFirst()
                .orElse(null);

    }
}
