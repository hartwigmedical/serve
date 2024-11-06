package com.hartwig.serve.common.ensemblcache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.serve.common.RefGenomeFunctions;
import com.hartwig.serve.common.serialization.SerializationUtil;
import com.hartwig.serve.datamodel.RefGenome;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EnsemblDataLoader {

    private static final Logger LOGGER = LogManager.getLogger(EnsemblDataLoader.class);

    private static final String ENSEMBL_GENE_DATA_FILE = "ensembl_gene_data.csv";
    private static final String ENSEMBL_TRANS_EXON_DATA_FILE = "ensembl_trans_exon_data.csv";

    private static final String ENSEMBL_FILE_DELIMITER = ",";

    private EnsemblDataLoader() {
    }

    @NotNull
    public static EnsemblDataCache load(@NotNull String dataPath, @NotNull RefGenome refGenome) throws IOException {
        String basePath = dataPath + File.separator;
        Map<String, List<GeneData>> genesPerChromosome = loadGeneData(basePath + ENSEMBL_GENE_DATA_FILE, refGenome);
        Map<String, List<TranscriptData>> transcriptsPerGeneId = loadTranscriptData(basePath + ENSEMBL_TRANS_EXON_DATA_FILE);

        return new EnsemblDataCache(genesPerChromosome, transcriptsPerGeneId);
    }

    @NotNull
    private static Map<String, List<GeneData>> loadGeneData(@NotNull String geneDataFile, @NotNull RefGenome version) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(geneDataFile));

        Map<String, Integer> fields = SerializationUtil.createFields(fileReader.readLine(), ENSEMBL_FILE_DELIMITER);

        int geneIdIndex = fields.get("GeneId");
        int geneNameIndex = fields.get("GeneName");
        int chromosomeIndex = fields.get("Chromosome");
        int strandIndex = fields.get("Strand");
        int geneStartIndex = fields.get("GeneStart");
        int geneEndIndex = fields.get("GeneEnd");
        int karyotypeBandIndex = fields.get("KaryotypeBand");

        Map<String, List<GeneData>> genesPerChromosome = Maps.newHashMap();
        List<GeneData> currentGenes = Lists.newArrayList();
        String currentChromosome = Strings.EMPTY;

        String line = fileReader.readLine();
        while (line != null) {
            String[] values = line.split(ENSEMBL_FILE_DELIMITER);

            String chromosome = RefGenomeFunctions.versionedChromosome(values[chromosomeIndex], version);
            if (currentChromosome.isEmpty()) {
                currentChromosome = chromosome;
            }

            if (!currentChromosome.equals(chromosome)) {
                updateGenesForChromosome(genesPerChromosome, currentChromosome, currentGenes);
                currentGenes = Lists.newArrayList();
            }

            currentChromosome = chromosome;

            currentGenes.add(ImmutableGeneData.builder()
                    .geneId(values[geneIdIndex])
                    .geneName(values[geneNameIndex])
                    .chromosome(chromosome)
                    .strand(Byte.parseByte(values[strandIndex]))
                    .geneStart(Integer.parseInt(values[geneStartIndex]))
                    .geneEnd(Integer.parseInt(values[geneEndIndex]))
                    .karyotypeBand(values[karyotypeBandIndex])
                    .build());

            line = fileReader.readLine();
        }

        // The final record doesn't get added automatically.
        updateGenesForChromosome(genesPerChromosome, currentChromosome, currentGenes);

        LOGGER.debug("Loaded {} genes from {}", countValues(genesPerChromosome), geneDataFile);

        return genesPerChromosome;
    }

    private static void updateGenesForChromosome(@NotNull Map<String, List<GeneData>> genesPerChromosome, @NotNull String chromosome,
            @NotNull List<GeneData> genes) {
        List<GeneData> existing = genesPerChromosome.get(chromosome);
        if (existing == null) {
            existing = Lists.newArrayList();
        }
        existing.addAll(genes);
        genesPerChromosome.put(chromosome, existing);
    }

    @NotNull
    private static Map<String, List<TranscriptData>> loadTranscriptData(@NotNull String transcriptDataFile) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(transcriptDataFile));

        Map<String, Integer> fields = SerializationUtil.createFields(fileReader.readLine(), ENSEMBL_FILE_DELIMITER);

        int geneIdIndex = fields.get("GeneId");
        int canonicalTransIdIndex = fields.get("CanonicalTranscriptId");
        int strandIndex = fields.get("Strand");
        int transIdIndex = fields.get("TransId");
        int transcriptNameIndex = fields.get("TransName");
        int biotypeIndex = fields.get("BioType");
        int transStartIndex = fields.get("TransStart");
        int transEndIndex = fields.get("TransEnd");
        int exonRankIndex = fields.get("ExonRank");
        int exonStartIndex = fields.get("ExonStart");
        int exonEndIndex = fields.get("ExonEnd");
        int exonPhaseIndex = fields.get("ExonPhase");
        int exonEndPhaseIndex = fields.get("ExonEndPhase");
        int codingStartIndex = fields.get("CodingStart");
        int codingEndIndex = fields.get("CodingEnd");

        Map<String, List<TranscriptData>> transcriptsPerGeneId = Maps.newHashMap();
        String currentGeneId = null;
        int currentTranscriptId = -1;
        TranscriptData previousTranscript = null;
        List<TranscriptData> currentTranscripts = Lists.newArrayList();
        List<ExonData> currentExons = Lists.newArrayList();

        String line = fileReader.readLine();
        while (line != null) {
            String[] values = line.split(ENSEMBL_FILE_DELIMITER);

            int transcriptId = Integer.parseInt(values[transIdIndex]);
            if (currentTranscriptId == -1) {
                currentTranscriptId = transcriptId;
            }

            if (transcriptId != currentTranscriptId) {
                currentTranscripts.add(ImmutableTranscriptData.builder().from(previousTranscript).exons(currentExons).build());
                currentExons = Lists.newArrayList();
            }

            currentTranscriptId = transcriptId;

            String geneId = values[geneIdIndex];
            if (currentGeneId == null) {
                currentGeneId = geneId;
            }

            if (!geneId.equals(currentGeneId)) {
                if (transcriptsPerGeneId.containsKey(currentGeneId)) {
                    throw new IllegalStateException("Attempt to load ensembl data twice for gene " + currentGeneId);
                }
                transcriptsPerGeneId.put(currentGeneId, currentTranscripts);
                currentTranscripts = Lists.newArrayList();
            }

            currentGeneId = geneId;

            previousTranscript = ImmutableTranscriptData.builder()
                    .transcriptId(transcriptId)
                    .transcriptName(values[transcriptNameIndex])
                    .geneId(geneId)
                    .isCanonical(Integer.parseInt(values[canonicalTransIdIndex]) == transcriptId)
                    .strand(Byte.parseByte(values[strandIndex]))
                    .transcriptStart(Integer.parseInt(values[transStartIndex]))
                    .transcriptEnd(Integer.parseInt(values[transEndIndex]))
                    .codingStart(nullableInt(values[codingStartIndex]))
                    .codingEnd(nullableInt(values[codingEndIndex]))
                    .bioType(values[biotypeIndex])
                    .build();

            ExonData exon = ImmutableExonData.builder()
                    .start(Integer.parseInt(values[exonStartIndex]))
                    .end(Integer.parseInt(values[exonEndIndex]))
                    .rank(Integer.parseInt(values[exonRankIndex]))
                    .phaseStart(Integer.parseInt(values[exonPhaseIndex]))
                    .phaseEnd(Integer.parseInt(values[exonEndPhaseIndex]))
                    .build();

            currentExons.add(exon);

            line = fileReader.readLine();
        }

        // The final record doesn't get added automatically, if is exists
        if (previousTranscript != null && currentGeneId != null) {
            currentTranscripts.add(ImmutableTranscriptData.builder().from(previousTranscript).exons(currentExons).build());
            transcriptsPerGeneId.put(currentGeneId, currentTranscripts);
        }

        LOGGER.debug("Loaded {} genes with {} transcripts and {} exons from {}",
                transcriptsPerGeneId.size(),
                countValues(transcriptsPerGeneId),
                countExons(transcriptsPerGeneId),
                transcriptDataFile);

        return transcriptsPerGeneId;
    }

    private static <T> int countValues(@NotNull Map<String, List<T>> objectMap) {
        int valueCount = 0;
        for (List<T> objects : objectMap.values()) {
            valueCount += objects.size();
        }
        return valueCount;
    }

    private static int countExons(@NotNull Map<String, List<TranscriptData>> transcriptsPerGeneId) {
        int exonCount = 0;
        for (List<TranscriptData> transcripts : transcriptsPerGeneId.values()) {
            for (TranscriptData transcript : transcripts) {
                exonCount += transcript.exons().size();
            }
        }
        return exonCount;
    }

    @Nullable
    private static Integer nullableInt(@NotNull String value) {
        return !value.equalsIgnoreCase("NULL") ? Integer.parseInt(value) : null;
    }
}
