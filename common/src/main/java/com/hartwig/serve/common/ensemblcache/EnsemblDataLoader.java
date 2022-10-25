package com.hartwig.serve.common.ensemblcache;

import static com.hartwig.serve.datamodel.serialization.util.SerializationUtil.createFields;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.refgenome.RefGenomeFunctions;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class EnsemblDataLoader {

    public static final String ENSEMBL_GENE_DATA_FILE = "ensembl_gene_data.csv";
    public static final String ENSEMBL_TRANS_EXON_DATA_FILE = "ensembl_trans_exon_data.csv";
    public static final String ENSEMBL_TRANS_SPLICE_DATA_FILE = "ensembl_trans_splice_data.csv";
    public static final String ENSEMBL_PROTEIN_FEATURE_DATA_FILE = "ensembl_protein_features.csv";

    private static final Logger LOGGER = LogManager.getLogger(EnsemblDataLoader.class);

    public static final String ENSEMBL_DELIM = ",";

    public static boolean loadEnsemblGeneData(final String dataPath, final List<String> restrictedGeneIds,
            final Map<String, List<GeneData>> chrGeneDataMap, RefGenomeVersion version) {
        return loadEnsemblGeneData(dataPath, restrictedGeneIds, chrGeneDataMap, version, false);
    }

    public static boolean loadEnsemblGeneData(final String dataPath, final List<String> restrictedGeneIds,
            final Map<String, List<GeneData>> chrGeneDataMap, RefGenomeVersion version, boolean loadSynonyms) {
        String filename = dataPath;

        filename += ENSEMBL_GENE_DATA_FILE;

        if (!Files.exists(Paths.get(filename))) {
            return false;
        }

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filename));

            String line = fileReader.readLine();

            final Map<String, Integer> fieldsIndexMap = createFields(line, ENSEMBL_DELIM);

            if (line == null) {
                LOGGER.error("empty Ensembl gene data file({})", filename);
                return false;
            }

            // GeneId,GeneName,Chromosome,Strand,GeneStart,GeneEnd,EntrezIds,KaryotypeBand,Synonyms
            int geneIdIndex = fieldsIndexMap.get("GeneId");
            int geneNameIndex = fieldsIndexMap.get("GeneName");
            int chromosomeIndex = fieldsIndexMap.get("Chromosome");
            int strandIndex = fieldsIndexMap.get("Strand");
            int geneStartIndex = fieldsIndexMap.get("GeneStart");
            int geneEndIndex = fieldsIndexMap.get("GeneEnd");
            int karyotypeBandIndex = fieldsIndexMap.get("KaryotypeBand");
            int synonymIndex = fieldsIndexMap.get("Synonyms");

            line = fileReader.readLine(); // skip header

            List<GeneData> geneList = null;
            String currentChr = "";
            int geneCount = 0;

            while (line != null) {
                String[] items = line.split(ENSEMBL_DELIM, -1);

                final String geneId = items[geneIdIndex];

                if (!restrictedGeneIds.isEmpty() && !restrictedGeneIds.contains(geneId)) {
                    line = fileReader.readLine();
                    continue;
                }

                final String chromosome = RefGenomeFunctions.versionedChromosome(items[chromosomeIndex], version);

                GeneData geneData = new GeneData(geneId,
                        items[geneNameIndex],
                        chromosome,
                        Byte.parseByte(items[strandIndex]),
                        Integer.parseInt(items[geneStartIndex]),
                        Integer.parseInt(items[geneEndIndex]),
                        items[karyotypeBandIndex]);

                if (loadSynonyms) {
                    geneData.setSynonyms(items[synonymIndex]);
                }

                if (!currentChr.equals(chromosome)) {
                    currentChr = chromosome;
                    geneList = chrGeneDataMap.computeIfAbsent(chromosome, k -> Lists.newArrayList());
                }

                geneList.add(geneData);
                ++geneCount;

                line = fileReader.readLine();
            }

            LOGGER.debug("loaded {} gene records", geneCount);
        } catch (IOException e) {
            LOGGER.warn("failed to load Ensembl gene ({}): {}", filename, e.toString());
            return false;
        }

        return true;
    }

    public static boolean loadTranscriptData(final String dataPath, Map<String, List<TranscriptData>> transcriptDataMap,
            final List<String> restrictedGeneIds, boolean cacheExons, boolean canonicalOnly, final List<String> nonCanonicalTrans) {
        String filename = dataPath;

        filename += ENSEMBL_TRANS_EXON_DATA_FILE;

        if (!Files.exists(Paths.get(filename))) {
            return false;
        }

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filename));

            String line = fileReader.readLine();

            final Map<String, Integer> fieldsIndexMap = createFields(line, ENSEMBL_DELIM);

            if (line == null) {
                LOGGER.error("empty Ensembl gene-exon data file({})", filename);
                return false;
            }

            // GeneId,CanonicalTranscriptId,Strand,TransId,TransName,BioType,TransStart,TransEnd,ExonRank,ExonStart,ExonEnd,ExonPhase,ExonEndPhase,CodingStart,CodingEnd
            int geneIdIndex = fieldsIndexMap.get("GeneId");
            int canonicalTransIdIndex = fieldsIndexMap.get("CanonicalTranscriptId");
            int strandIndex = fieldsIndexMap.get("Strand");
            int transIdIndex = fieldsIndexMap.get("TransId");
            int transNameIndex = fieldsIndexMap.containsKey("TransName") ? fieldsIndexMap.get("TransName") : fieldsIndexMap.get("Trans");
            int biotypeIndex = fieldsIndexMap.get("BioType");
            int transStartIndex = fieldsIndexMap.get("TransStart");
            int transEndIndex = fieldsIndexMap.get("TransEnd");
            int exonRankIndex = fieldsIndexMap.get("ExonRank");
            int exonStartIndex = fieldsIndexMap.get("ExonStart");
            int exonEndIndex = fieldsIndexMap.get("ExonEnd");
            int exonPhaseIndex = fieldsIndexMap.get("ExonPhase");
            int exonEndPhaseIndex = fieldsIndexMap.get("ExonEndPhase");
            int codingStartIndex = fieldsIndexMap.get("CodingStart");
            int codingEndIndex = fieldsIndexMap.get("CodingEnd");

            int exonCount = 0;
            int transcriptCount = 0;
            String currentGene = "";
            TranscriptData currentTrans = null;
            String lastSkippedGeneId = "";
            List<TranscriptData> transDataList = null;
            List<ExonData> exonDataList = null;

            while (line != null) {
                line = fileReader.readLine();

                if (line == null) {
                    break;
                }

                String[] items = line.split(ENSEMBL_DELIM);

                final String geneId = items[geneIdIndex];
                int transId = Integer.parseInt(items[transIdIndex]);

                if (lastSkippedGeneId.equals(geneId) || (!restrictedGeneIds.isEmpty() && !restrictedGeneIds.contains(geneId))) {
                    lastSkippedGeneId = geneId;
                    continue;
                }

                if (!geneId.equals(currentGene)) {
                    currentGene = geneId;
                    transDataList = Lists.newArrayList();
                    transcriptDataMap.put(geneId, transDataList);
                }

                if (currentTrans == null || currentTrans.TransId != transId) {
                    int canonicalTransId = Integer.parseInt(items[canonicalTransIdIndex]);
                    boolean isCanonical = (canonicalTransId == transId);
                    String transName = items[transNameIndex];

                    if (canonicalOnly) {
                        if (!isCanonical && !nonCanonicalTrans.contains(transName)) {
                            continue;
                        }
                    }

                    exonDataList = Lists.newArrayList();

                    Integer codingStart =
                            !items[codingStartIndex].equalsIgnoreCase("NULL") ? Integer.parseInt(items[codingStartIndex]) : null;
                    Integer codingEnd = !items[codingEndIndex].equalsIgnoreCase("NULL") ? Integer.parseInt(items[codingEndIndex]) : null;

                    currentTrans = new TranscriptData(transId,
                            transName,
                            geneId,
                            isCanonical,
                            Byte.parseByte(items[strandIndex]),
                            Integer.parseInt(items[transStartIndex]),
                            Integer.parseInt(items[transEndIndex]),
                            codingStart,
                            codingEnd,
                            items[biotypeIndex]);

                    ++transcriptCount;

                    currentTrans.setExons(exonDataList);
                    transDataList.add(currentTrans);
                }

                if (cacheExons || currentTrans.IsCanonical) {
                    ExonData exonData = new ExonData(transId,
                            Integer.parseInt(items[exonStartIndex]),
                            Integer.parseInt(items[exonEndIndex]),
                            Integer.parseInt(items[exonRankIndex]),
                            Integer.parseInt(items[exonPhaseIndex]),
                            Integer.parseInt(items[exonEndPhaseIndex]));

                    exonDataList.add(exonData);
                    ++exonCount;
                }
            }

            LOGGER.debug("loaded {} genes with {} transcripts records and {} exons", transcriptDataMap.size(), transcriptCount, exonCount);
        } catch (IOException e) {
            LOGGER.warn("failed to load gene transcript exon data({}): {}", filename, e.toString());
            return false;
        }

        return true;
    }

    public static boolean loadTranscriptProteinData(final String dataPath, Map<Integer, List<TranscriptProteinData>> proteinDataMap,
            Set<Integer> restrictedTransIds) {
        String filename = dataPath;

        filename += ENSEMBL_PROTEIN_FEATURE_DATA_FILE;

        if (!Files.exists(Paths.get(filename))) {
            return false;
        }

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filename));

            String line = fileReader.readLine();

            final Map<String, Integer> fieldsIndexMap = createFields(line, ENSEMBL_DELIM);

            if (line == null) {
                LOGGER.error("empty Ensembl protein feature data file({})", filename);
                return false;
            }

            // TranscriptId,TranslationId,ProteinFeatureId,SeqStart,SeqEnd,HitDescription
            int transIdIndex = fieldsIndexMap.get("TranscriptId");
            int translationIdIndex = fieldsIndexMap.get("TranslationId");
            int pfIdIndex = fieldsIndexMap.get("ProteinFeatureId");
            int pfStartIndex = fieldsIndexMap.get("SeqStart");
            int pfEndIndex = fieldsIndexMap.get("SeqEnd");
            int pfDescIndex = fieldsIndexMap.get("HitDescription");

            int proteinCount = 0;
            int currentTransId = -1;
            List<TranscriptProteinData> transProteinDataList = null;

            line = fileReader.readLine(); // skip header

            while (line != null) {
                // parse CSV data
                String[] items = line.split(ENSEMBL_DELIM);

                // check if still on the same variant
                int transId = Integer.parseInt(items[transIdIndex]);

                if (!restrictedTransIds.isEmpty() && !restrictedTransIds.contains(transId)) {
                    line = fileReader.readLine();
                    continue;
                }

                if (transId != currentTransId) {
                    currentTransId = transId;
                    transProteinDataList = Lists.newArrayList();
                    proteinDataMap.put(transId, transProteinDataList);
                }

                TranscriptProteinData proteinData = new TranscriptProteinData(transId,
                        Integer.parseInt(items[translationIdIndex]),
                        Integer.parseInt(items[pfIdIndex]),
                        Integer.parseInt(items[pfStartIndex]),
                        Integer.parseInt(items[pfEndIndex]),
                        items[pfDescIndex]);

                transProteinDataList.add(proteinData);
                ++proteinCount;

                line = fileReader.readLine();
            }

            LOGGER.debug("loaded {} protein trans records with {} locations", proteinDataMap.size(), proteinCount);
        } catch (IOException e) {
            LOGGER.warn("failed to load transcript protein features({}): {}", filename, e.toString());
            return false;
        }

        return true;
    }

    public static boolean loadTranscriptSpliceAcceptorData(final String dataPath, Map<Integer, Integer> transSaPositionDataMap,
            final Set<Integer> restrictedTransIds) {
        String filename = dataPath;

        filename += ENSEMBL_TRANS_SPLICE_DATA_FILE;

        if (!Files.exists(Paths.get(filename))) {
            return false;
        }

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filename));

            String line = fileReader.readLine();

            final Map<String, Integer> fieldsIndexMap = createFields(line, ENSEMBL_DELIM);

            if (line == null) {
                LOGGER.error("empty Ensembl trans splice acceptor data file({})", filename);
                return false;
            }

            // GeneId,TransId,TransName,TransStartPos,PreSpliceAcceptorPosition,Distance
            int transIdIndex = fieldsIndexMap.get("TransId");
            int saPositionIndex = fieldsIndexMap.get("PreSpliceAcceptorPosition");

            line = fileReader.readLine(); // skip header

            while (line != null) {
                String[] items = line.split(ENSEMBL_DELIM);

                final int transId = Integer.parseInt(items[transIdIndex]);

                if (!restrictedTransIds.isEmpty() && !restrictedTransIds.contains(transId)) {
                    line = fileReader.readLine();
                    continue;
                }

                int saPosition = Integer.parseInt(items[saPositionIndex]);

                transSaPositionDataMap.put(transId, saPosition);

                line = fileReader.readLine();
            }

            LOGGER.debug("loaded {} trans splice-acceptor position records", transSaPositionDataMap.size());
        } catch (IOException e) {
            LOGGER.warn("failed to load transcript splice data({}): {}", filename, e.toString());
            return false;
        }

        return true;
    }
}
