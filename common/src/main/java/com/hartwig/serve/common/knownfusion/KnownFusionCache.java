package com.hartwig.serve.common.knownfusion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.serve.datamodel.util.FileReaderUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class KnownFusionCache {

    private static final Logger LOGGER = LogManager.getLogger(KnownFusionCache.class);

    private static final String FILE_DELIMITER = ",";

    @NotNull
    private final List<KnownFusionData> data;
    @NotNull
    private final Map<KnownFusionType, List<KnownFusionData>> dataByType;

    // cached since so commonly checked
    private final List<KnownFusionData> knownPairData;

    public KnownFusionCache() {
        data = Lists.newArrayList();
        dataByType = Maps.newHashMap();
        knownPairData = Lists.newArrayList();

        // initialise to avoid having to check for null
        Arrays.stream(KnownFusionType.values())
                .filter(x -> x != KnownFusionType.NONE)
                .forEach(x -> dataByType.put(x, Lists.newArrayList()));
    }

    @NotNull
    public List<KnownFusionData> knownFusions() {
        return data;
    }

    public boolean hasKnownFusion(@NotNull String fiveGene, @NotNull String threeGene) {
        return dataByType.get(KnownFusionType.KNOWN_PAIR)
                .stream()
                .anyMatch(x -> x.FiveGene.equals(fiveGene) && x.ThreeGene.equals(threeGene));
    }

    public boolean hasKnownIgFusion(@NotNull String fiveGene, @NotNull String threeGene) {
        return dataByType.get(KnownFusionType.IG_KNOWN_PAIR)
                .stream()
                .anyMatch(x -> x.FiveGene.equals(fiveGene) && x.ThreeGene.equals(threeGene));
    }

    public boolean hasPromiscuousIgFusion(@NotNull String gene) {
        return dataByType.get(KnownFusionType.IG_PROMISCUOUS).stream().anyMatch(x -> x.FiveGene.equals(gene));
    }

    public boolean hasPromiscuousFiveGene(@NotNull String gene) {
        return dataByType.get(KnownFusionType.PROMISCUOUS_5).stream().anyMatch(x -> x.FiveGene.equals(gene));
    }

    public boolean hasPromiscuousThreeGene(@NotNull String gene) {
        return dataByType.get(KnownFusionType.PROMISCUOUS_3).stream().anyMatch(x -> x.ThreeGene.equals(gene));
    }

    public boolean hasAnyIgFusion(@NotNull String gene) {
        return dataByType.get(KnownFusionType.IG_KNOWN_PAIR).stream().anyMatch(x -> x.FiveGene.equals(gene) || x.ThreeGene.equals(gene));
    }

    public boolean hasExonDelDup(@NotNull String gene) {
        return dataByType.get(KnownFusionType.EXON_DEL_DUP).stream().anyMatch(x -> x.FiveGene.equals(gene) && x.ThreeGene.equals(gene));
    }

    public boolean hasKnownPairGene(@NotNull String gene) {
        return knownPairData.stream().anyMatch(x -> x.FiveGene.equals(gene) || x.ThreeGene.equals(gene));
    }

    public void addData(@NotNull KnownFusionData data) {
        this.data.add(data);
        dataByType.get(data.Type).add(data);

        if (data.Type == KnownFusionType.KNOWN_PAIR) {
            knownPairData.add(data);
        }
    }

    public boolean loadFile(@NotNull String filename) {
        if (!Files.exists(Paths.get(filename))) {
            LOGGER.error("file({}) not found", filename);
            return false;
        }

        try {
            List<String> fileContents = Files.readAllLines(new File(filename).toPath());

            if (fileContents.isEmpty()) {
                return false;
            }

            Map<String, Integer> fieldIndexMap = FileReaderUtils.createFields(fileContents.get(0), FILE_DELIMITER);
            fileContents.remove(0);

            for (String data : fileContents) {
                try {
                    KnownFusionData knownFusionData = KnownFusionData.fromCsv(data, fieldIndexMap);
                    addData(knownFusionData);
                } catch (Exception e) {
                    LOGGER.error("file({}) invalid known fusion data will be skipped: {}", filename, data);
                }
            }
        } catch (IOException e) {
            LOGGER.error("file({}) invalid known fusion data: {}", filename, e.toString());
            return false;
        }

        return true;
    }
}
