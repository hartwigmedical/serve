package com.hartwig.serve.common.knownfusion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class KnownFusionCacheLoader {

    private static final Logger LOGGER = LogManager.getLogger(KnownFusionCacheLoader.class);

    private static final String FIELD_DELIMITER = ",";

    private static final String FLD_TYPE = "Type";
    private static final String FLD_FIVE_GENE = "FiveGene";
    private static final String FLD_THREE_GENE = "ThreeGene";
    private static final String FLD_PUB_MED = "PubMedId";
    private static final String FLD_CANCER_TYPES = "CancerTypes";
    private static final String FLD_HIGH_IMPACT_PROM = "HighImpactPromiscuous";

    private KnownFusionCacheLoader() {
    }

    @NotNull
    public static KnownFusionCache load(@NotNull String knownFusionFile) throws IOException {
        List<String> lines = Files.readAllLines(new File(knownFusionFile).toPath());

        List<KnownFusionData> knownFusions = Lists.newArrayList();

        Map<String, Integer> fields = SerializationUtil.createFields(lines.get(0), FIELD_DELIMITER);

        for (String line : lines.subList(1, lines.size())) {
            knownFusions.add(fromLine(line, fields));
        }

        Map<KnownFusionType, List<KnownFusionData>> knownFusionsByType = Maps.newHashMap();
        for (KnownFusionType type : KnownFusionType.values()) {
            if (type != KnownFusionType.NONE) {
                knownFusionsByType.put(type, Lists.newArrayList());
            }
        }

        for (KnownFusionData knownFusion : knownFusions) {
            if (knownFusion.type() != KnownFusionType.NONE) {
                List<KnownFusionData> current = knownFusionsByType.get(knownFusion.type());
                current.add(knownFusion);
                knownFusionsByType.put(knownFusion.type(), current);
            }
        }

        return new KnownFusionCache(knownFusions, knownFusionsByType);
    }

    @NotNull
    private static KnownFusionData fromLine(@NotNull String line, @NotNull Map<String, Integer> fields) {
        String[] values = line.split(FIELD_DELIMITER);

        try {
            return ImmutableKnownFusionData.builder()
                    .type(KnownFusionType.valueOf(values[fields.get(FLD_TYPE)]))
                    .fiveGene(values[fields.get(FLD_FIVE_GENE)])
                    .threeGene(values[fields.get(FLD_THREE_GENE)])
                    .cancerTypes(values[fields.get(FLD_CANCER_TYPES)])
                    .pubMedId(values[fields.get(FLD_PUB_MED)])
                    .highImpactPromiscuous(values[fields.get(FLD_HIGH_IMPACT_PROM)].equalsIgnoreCase("TRUE"))
                    .build();
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.error("Unable to parse line [{}] with fiels [{}]. Check the known_fusions CSV",
                    line,
                    fields.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.joining(",")));
            throw new RuntimeException(e);
        }
    }
}
