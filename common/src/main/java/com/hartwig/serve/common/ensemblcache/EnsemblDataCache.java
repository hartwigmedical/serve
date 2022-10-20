package com.hartwig.serve.common.ensemblcache;

import static com.hartwig.serve.common.ensemblcache.EnsemblDataLoader.loadEnsemblGeneData;
import static com.hartwig.serve.common.ensemblcache.EnsemblDataLoader.loadTranscriptProteinData;
import static com.hartwig.serve.common.ensemblcache.EnsemblDataLoader.loadTranscriptSpliceAcceptorData;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.common.genome.GeneData;
import com.hartwig.serve.common.genome.TranscriptData;
import com.hartwig.serve.common.genome.TranscriptProteinData;
import com.hartwig.serve.datamodel.genome.refgenome.RefGenomeVersion;

import org.jetbrains.annotations.NotNull;

public class EnsemblDataCache {

    private final String mDataPath;
    private final RefGenomeVersion mRefGenomeVersion;

    private final Map<String, List<TranscriptData>> mTranscriptByGeneIdMap; // transcripts keyed by geneId
    private final Map<String, List<GeneData>> mChrGeneDataMap; // genes keyed by chromosome
    private final Map<Integer, List<TranscriptProteinData>> mEnsemblProteinDataMap;
    private final Map<Integer, Integer> mTransSpliceAcceptorPosDataMap;
    private final Map<String, GeneData> mGeneNameIdMap; // for faster look-up by name

    // whether to load more details information for each transcript - exons, protein domains, splice positions etc
    private boolean mRequireExons;
    private boolean mRequireProteinDomains;
    private boolean mRequireSplicePositions;
    private boolean mCanonicalTranscriptsOnly;
    private boolean mRequireGeneSynonyms;

    private final List<String> mRestrictedGeneIdList = Lists.newArrayList();

    public EnsemblDataCache(@NotNull String dataPath, @NotNull RefGenomeVersion refGenomeVersion) {
        mDataPath = checkAddDirSeparator(dataPath);
        mRefGenomeVersion = refGenomeVersion;

        mTranscriptByGeneIdMap = Maps.newHashMap();
        // transcripts keyed by transId
        mChrGeneDataMap = Maps.newHashMap();
        mEnsemblProteinDataMap = Maps.newHashMap();
        mTransSpliceAcceptorPosDataMap = Maps.newHashMap();
        // keyed by geneId
        mGeneNameIdMap = Maps.newHashMap();
        mRequireExons = true;
        mRequireProteinDomains = false;
        mRequireSplicePositions = false;
        mCanonicalTranscriptsOnly = false;
        mRequireGeneSynonyms = false;
    }

    public void setRequiredData(boolean exons, boolean proteinDomains, boolean splicePositions, boolean canonicalOnly) {
        mRequireExons = exons;
        mRequireSplicePositions = splicePositions;
        mRequireProteinDomains = proteinDomains;
        mCanonicalTranscriptsOnly = canonicalOnly;
    }

    public Map<String, List<GeneData>> getChrGeneDataMap() {
        return mChrGeneDataMap;
    }

    public final GeneData getGeneDataByName(@NotNull String geneName) {
        if (!mGeneNameIdMap.isEmpty()) {
            return mGeneNameIdMap.get(geneName);
        }

        return getGeneData(geneName, true);
    }

    private GeneData getGeneData(@NotNull String gene, boolean byName) {
        for (Map.Entry<String, List<GeneData>> entry : mChrGeneDataMap.entrySet()) {
            for (GeneData geneData : entry.getValue()) {
                if ((byName && geneData.GeneName.equals(gene)) || (!byName && geneData.GeneId.equals(gene))) {
                    return geneData;
                }
            }
        }

        return null;
    }

    public List<TranscriptData> getTranscripts(@NotNull String geneId) {
        return mTranscriptByGeneIdMap.get(geneId);
    }

    public TranscriptData getCanonicalTranscriptData(@NotNull String geneId) {
        return getTranscriptData(geneId, "");
    }

    public TranscriptData getTranscriptData(@NotNull String geneId, @NotNull String transcriptId) {
        // leave transcriptId empty to retrieve the canonical transcript
        final List<TranscriptData> transDataList = mTranscriptByGeneIdMap.get(geneId);

        if (transDataList == null || transDataList.isEmpty()) {
            return null;
        }

        for (final TranscriptData transData : transDataList) {
            if (transcriptId.isEmpty() && transData.IsCanonical) {
                return transData;
            } else if (transData.TransName.equals(transcriptId)) {
                return transData;
            }
        }

        return null;
    }

    public boolean load(boolean delayTranscriptLoading) {
        if (!loadEnsemblGeneData(mDataPath, mRestrictedGeneIdList, mChrGeneDataMap, mRefGenomeVersion, mRequireGeneSynonyms)) {
            return false;
        }

        if (!delayTranscriptLoading) {
            if (!EnsemblDataLoader.loadTranscriptData(mDataPath,
                    mTranscriptByGeneIdMap,
                    mRestrictedGeneIdList,
                    mRequireExons,
                    mCanonicalTranscriptsOnly,
                    Lists.newArrayList())) {
                return false;
            }

            if (mRequireProteinDomains && !loadTranscriptProteinData(mDataPath, mEnsemblProteinDataMap, Sets.newHashSet())) {
                return false;
            }

            if (mRequireSplicePositions && !loadTranscriptSpliceAcceptorData(mDataPath,
                    mTransSpliceAcceptorPosDataMap,
                    Sets.newHashSet())) {
                return false;
            }
        }

        return true;
    }

    public Map<String, String> createTransGeneNamesMap() {
        Map<String, String> transGeneMap = Maps.newHashMap();

        for (List<GeneData> geneDataList : mChrGeneDataMap.values()) {
            for (GeneData geneData : geneDataList) {
                List<TranscriptData> transDataList = getTranscripts(geneData.GeneId);

                for (TranscriptData tranData : transDataList) {
                    if (tranData.IsCanonical) {
                        transGeneMap.put(tranData.TransName, geneData.GeneName);
                    }
                }
            }
        }

        return transGeneMap;
    }

    @NotNull
    private static String checkAddDirSeparator(@NotNull String outputDir) {
        if (outputDir.endsWith(File.separator)) {
            return outputDir;
        }

        return outputDir + File.separator;
    }
}
