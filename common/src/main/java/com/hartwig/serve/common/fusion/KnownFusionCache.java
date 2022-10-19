package com.hartwig.serve.common.fusion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.serve.common.utils.FileReaderUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KnownFusionCache {

    private final List<KnownFusionData> mData;
    private final Map<KnownFusionType, List<KnownFusionData>> mDataByType;

    // cached since so commonly checked
    private final List<KnownFusionData> mKnownPairData;
    private final List<KnownFusionData> mIgRegionData;
    private final List<KnownFusionData> mHighImpactPromiscuousData;

    private static final String FILE_DELIMITER = ",";

    public static final Logger KF_LOGGER = LogManager.getLogger(KnownFusionCache.class);

    public KnownFusionCache() {
        mData = Lists.newArrayList();
        mDataByType = Maps.newHashMap();
        mIgRegionData = Lists.newArrayList();
        mKnownPairData = Lists.newArrayList();
        mHighImpactPromiscuousData = Lists.newArrayList();

        // initialise to avoid having to check for null
        Arrays.stream(KnownFusionType.values()).filter(x -> x != KnownFusionType.NONE).forEach(x -> mDataByType.put(x, Lists.newArrayList()));
    }

    public final List<KnownFusionData> getData() {
        return mData;
    }

    public boolean hasKnownFusion(final String fiveGene, final String threeGene) {
        return mDataByType.get(KnownFusionType.KNOWN_PAIR).stream().anyMatch(x -> x.FiveGene.equals(fiveGene) && x.ThreeGene.equals(threeGene));
    }

    public boolean hasKnownIgFusion(final String fiveGene, final String threeGene) {
        return mDataByType.get(KnownFusionType.IG_KNOWN_PAIR).stream().anyMatch(x -> x.FiveGene.equals(fiveGene) && x.ThreeGene.equals(threeGene));
    }

    public boolean hasPromiscuousIgFusion(final String gene) {
        return mDataByType.get(KnownFusionType.IG_PROMISCUOUS).stream().anyMatch(x -> x.FiveGene.equals(gene));
    }

    public boolean hasPromiscuousFiveGene(final String gene) {
        return mDataByType.get(KnownFusionType.PROMISCUOUS_5).stream().anyMatch(x -> x.FiveGene.equals(gene));
    }

    public boolean hasPromiscuousThreeGene(final String gene) {
        return mDataByType.get(KnownFusionType.PROMISCUOUS_3).stream().anyMatch(x -> x.ThreeGene.equals(gene));
    }

    public boolean hasAnyIgFusion(final String gene) {
        return mDataByType.get(KnownFusionType.IG_KNOWN_PAIR).stream().anyMatch(x -> x.FiveGene.equals(gene) || x.ThreeGene.equals(gene));
    }

    public boolean hasExonDelDup(final String gene) {
        return mDataByType.get(KnownFusionType.EXON_DEL_DUP).stream().anyMatch(x -> x.FiveGene.equals(gene) && x.ThreeGene.equals(gene));
    }

    public boolean hasKnownPairGene(final String gene) {
        return mKnownPairData.stream().anyMatch(x -> x.FiveGene.equals(gene) || x.ThreeGene.equals(gene));
    }

    public void addData(final KnownFusionData data) {
        mData.add(data);
        mDataByType.get(data.Type).add(data);

        if (data.Type == KnownFusionType.KNOWN_PAIR) {
            mKnownPairData.add(data);
        }

        if (data.igRegion() != null) {
            mIgRegionData.add(data);
        }

        if (data.isHighImpactPromiscuous()) {
            mHighImpactPromiscuousData.add(data);
        }
    }

    public boolean loadFile(final String filename) {
        if (!Files.exists(Paths.get(filename))) {
            KF_LOGGER.error("file({}) not found", filename);
            return false;
        }

        try {
            final List<String> fileContents = Files.readAllLines(new File(filename).toPath());

            if (fileContents.isEmpty()) {
                return false;
            }

            final Map<String, Integer> fieldIndexMap = FileReaderUtils.createFieldsIndexMap(fileContents.get(0), FILE_DELIMITER);
            fileContents.remove(0);

            for (String data : fileContents) {
                try {
                    KnownFusionData knownFusionData = KnownFusionData.fromCsv(data, fieldIndexMap);
                    addData(knownFusionData);
                } catch (Exception e) {
                    KF_LOGGER.error("file({}) invalid known fusion data will be skipped: {}", filename, data);
                }
            }
        } catch (IOException e) {
            KF_LOGGER.error("file({}) invalid known fusion data: {}", filename, e.toString());
            return false;
        }

        return true;
    }
}
