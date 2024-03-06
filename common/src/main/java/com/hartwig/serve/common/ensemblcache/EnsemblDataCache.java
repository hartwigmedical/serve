package com.hartwig.serve.common.ensemblcache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnsemblDataCache {

    @NotNull
    private final Map<String, List<GeneData>> genesPerChromosome;
    @NotNull
    private final Map<String, Map<String, GeneData>> genesPerChromosomeAndGeneName;
    @NotNull
    private final Map<String, List<TranscriptData>> transcriptsPerGeneId;

    public EnsemblDataCache(@NotNull final Map<String, List<GeneData>> genesPerChromosome,
            @NotNull final Map<String, List<TranscriptData>> transcriptsPerGeneId) {
        this.genesPerChromosome = genesPerChromosome;
        this.transcriptsPerGeneId = transcriptsPerGeneId;
        this.genesPerChromosomeAndGeneName = new HashMap<>();
        for (Map.Entry<String, List<GeneData>> geneByChromosome : this.genesPerChromosome.entrySet()) {
            Map<String, GeneData> geneMap = new HashMap<>();
            for (GeneData gene : geneByChromosome.getValue()) {
                geneMap.put(gene.geneName(), gene);
            }
            this.genesPerChromosomeAndGeneName.put(geneByChromosome.getKey(), geneMap);
        }
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
        for (Map<String, GeneData> genesPerChromosome : genesPerChromosomeAndGeneName.values()) {
            if (genesPerChromosome.containsKey(geneNameToFind)) {
                return genesPerChromosome.get(geneNameToFind);
            }
        }

        return null;
    }

    @Nullable
    public List<TranscriptData> transcriptsForGeneId(@NotNull String geneId) {
        return transcriptsPerGeneId.get(geneId);
    }

    @Nullable
    public TranscriptData findCanonicalTranscript(@NotNull String geneId) {
        List<TranscriptData> transcripts = transcriptsPerGeneId.get(geneId);

        if (transcripts == null) {
            return null;
        }

        for (TranscriptData transcript : transcripts) {
            if (transcript.isCanonical()) {
                return transcript;
            }
        }

        return null;
    }
}
