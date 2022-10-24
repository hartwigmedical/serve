package com.hartwig.serve.common.knownfusion;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class KnownFusionData {

    private static final Logger LOGGER = LogManager.getLogger(KnownFusionData.class);

    public final KnownFusionType Type;
    public final String FiveGene;
    public final String ThreeGene;
    public final String CancerTypes;
    public final String PubMedId;

    // type-specific data:
    public boolean mHighImpactPromiscuous;

    private static final String FLD_TYPE = "Type";
    private static final String FLD_FIVE_GENE = "FiveGene";
    private static final String FLD_THREE_GENE = "ThreeGene";
    private static final String FLD_PUB_MED = "PubMedId";
    private static final String FLD_CANCER_TYPES = "CancerTypes";
    private static final String FLD_HIGH_IMPACT_PROM = "HighImpactPromiscuous";
    private static final String FLD_OVERRIDES = "Overrides";

    public static final String OVERRIDE_ALTS = "ALTS";
    public static final String ALT_DATA = "ALT";

    private static final String FILE_DELIM = ",";
    private static final String ITEM_DELIM = ";";
    private static final String OVERRIDES_DELIM = " ";
    private static final String OVERRIDES_ID_DELIM = "=";

    public KnownFusionData(final KnownFusionType type, final String fiveGene, final String threeGene, final String cancerTypes,
            final String pubMedId) {
        Type = type;
        FiveGene = fiveGene;
        ThreeGene = threeGene;
        CancerTypes = cancerTypes;
        PubMedId = pubMedId;

        mHighImpactPromiscuous = false;
    }

    public static KnownFusionData fromCsv(final String data, final Map<String, Integer> fieldIndexMap) {
        final String[] items = data.split(FILE_DELIM, -1);

        KnownFusionData kfData = new KnownFusionData(KnownFusionType.valueOf(items[fieldIndexMap.get(FLD_TYPE)]),
                items[fieldIndexMap.get(FLD_FIVE_GENE)],
                items[fieldIndexMap.get(FLD_THREE_GENE)],
                items[fieldIndexMap.get(FLD_CANCER_TYPES)],
                items[fieldIndexMap.get(FLD_PUB_MED)]);

        try {
            if (items[fieldIndexMap.get(FLD_HIGH_IMPACT_PROM)].equalsIgnoreCase("TRUE")) {
                kfData.setHighImpactPromiscuous();
            }

            final String overrides = items[fieldIndexMap.get(FLD_OVERRIDES)];

            if (!overrides.isEmpty()) {
                kfData.applyOverrides(overrides);
            }
        } catch (Exception e) {
            LOGGER.error("failed to parse specific data for known fusion({}): error({})", kfData, e.toString());
        }

        return kfData;
    }

    public void applyOverrides(@NotNull String overrides) {
        for (String overrideItem : overrides.split(OVERRIDES_DELIM)) {
            String overrideName = overrideItem.split(OVERRIDES_ID_DELIM)[0];
            String overrideData = overrideItem.split(OVERRIDES_ID_DELIM)[1];

            if (overrideName.equals(OVERRIDE_ALTS)) {
                String[] altItems = overrideData.split(ITEM_DELIM);
                int index = 0;
                while (index < altItems.length) {
                    if (!altItems[index].equals(ALT_DATA) || altItems.length - index < 4) {
                        return;
                    }

                    ++index;
                    index += 3;
                }
            }
        }
    }

    public void setHighImpactPromiscuous() {
        mHighImpactPromiscuous = true;
    }

    public String toString() {
        return String.format("%s: genes(%s - %s) ct(%s)", Type, FiveGene, ThreeGene, CancerTypes);
    }
}
