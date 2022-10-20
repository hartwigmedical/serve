package com.hartwig.serve.common.knownfusion;

import static com.hartwig.serve.common.knownfusion.KnownFusionCache.KF_LOGGER;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class KnownFusionData {

    private static final int FS_UP = 0;
    private static final int FS_DOWN = 1;

    public final KnownFusionType Type;
    public final String FiveGene;
    public final String ThreeGene;
    public final String CancerTypes;
    public final String PubMedId;

    // type-specific data:
    public boolean mHighImpactPromiscuous;

    private final int[] mDownstreamDistance; // used for some known pair 5' genes and IG 3' genes

    private final int[] mFiveExonRange;
    private final int[] mThreeExonRange;

    // IG region
    private ChrBaseRegion mIgRegion;
    private byte mIgStrand;

    // 3' gene alternative mappings
    private final List<ChrBaseRegion> mThreeGeneAltRegions;

    private static final String FLD_TYPE = "Type";
    private static final String FLD_FIVE_GENE = "FiveGene";
    private static final String FLD_THREE_GENE = "ThreeGene";
    private static final String FLD_PUB_MED = "PubMedId";
    private static final String FLD_CANCER_TYPES = "CancerTypes";
    private static final String FLD_KNOWN_EXON_TRANS = "KnownExonTranscript";
    private static final String FLD_KNOWN_EXON_UP_RANGE = "KnownExonUpRange";
    private static final String FLD_KNOWN_EXON_DOWN_RANGE = "KnownExonDownRange";
    private static final String FLD_HIGH_IMPACT_PROM = "HighImpactPromiscuous";
    private static final String FLD_OVERRIDES = "Overrides";

    public static final String OVERRIDE_IG_RANGE = "IG_RANGE";
    public static final String OVERRIDE_ALTS = "ALTS";
    public static final String OVERRIDE_UP_DISTANCE = "UP_GENE_DOWNSTREAM_DISTANCE";
    public static final String OVERRIDE_DOWN_DISTANCE = "DOWN_GENE_DOWNSTREAM_DISTANCE";
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
        mFiveExonRange = new int[StartEndIterator.SE_PAIR];
        mThreeExonRange = new int[StartEndIterator.SE_PAIR];
        mIgRegion = null;
        mIgStrand = 0;
        mDownstreamDistance = new int[] { 0, 0 };
        mThreeGeneAltRegions = Lists.newArrayList();
    }

    public static KnownFusionData fromCsv(final String data, final Map<String, Integer> fieldIndexMap) {
        final String[] items = data.split(FILE_DELIM, -1);

        KnownFusionData kfData = new KnownFusionData(KnownFusionType.valueOf(items[fieldIndexMap.get(FLD_TYPE)]),
                items[fieldIndexMap.get(FLD_FIVE_GENE)],
                items[fieldIndexMap.get(FLD_THREE_GENE)],
                items[fieldIndexMap.get(FLD_CANCER_TYPES)],
                items[fieldIndexMap.get(FLD_PUB_MED)]);

        final String knownExonTrans = items[fieldIndexMap.get(FLD_KNOWN_EXON_TRANS)];
        final String knownExonUpRange = items[fieldIndexMap.get(FLD_KNOWN_EXON_UP_RANGE)];
        final String knownExonDownRange = items[fieldIndexMap.get(FLD_KNOWN_EXON_DOWN_RANGE)];

        try {
            if (!knownExonTrans.isEmpty()) {
                kfData.setKnownExonData(knownExonTrans, knownExonUpRange, knownExonDownRange);
            }

            if (items[fieldIndexMap.get(FLD_HIGH_IMPACT_PROM)].equalsIgnoreCase("TRUE")) {
                kfData.setHighImpactPromiscuous();
            }

            final String overrides = items[fieldIndexMap.get(FLD_OVERRIDES)];

            if (!overrides.isEmpty()) {
                kfData.applyOverrides(overrides);
            }
        } catch (Exception e) {
            KF_LOGGER.error("failed to parse specific data for known fusion({}): error({})", kfData, e.toString());
        }

        return kfData;
    }

    public void setKnownExonData(final String knownExonTrans, final String knownExonUpRange, final String knownExonDownRange) {
        if (!knownExonUpRange.isEmpty()) {
            final String[] exons = knownExonUpRange.split(ITEM_DELIM);
            mFiveExonRange[StartEndIterator.SE_START] = Integer.parseInt(exons[StartEndIterator.SE_START]);
            mFiveExonRange[StartEndIterator.SE_END] = Integer.parseInt(exons[StartEndIterator.SE_END]);
        }

        if (!knownExonDownRange.isEmpty()) {
            final String[] exons = knownExonDownRange.split(ITEM_DELIM);
            mThreeExonRange[StartEndIterator.SE_START] = Integer.parseInt(exons[StartEndIterator.SE_START]);
            mThreeExonRange[StartEndIterator.SE_END] = Integer.parseInt(exons[StartEndIterator.SE_END]);
        }
    }

    public void applyOverrides(final String overrides) {
        for (final String overrideItem : overrides.split(OVERRIDES_DELIM)) {
            final String overrideName = overrideItem.split(OVERRIDES_ID_DELIM)[0];
            final String overrideData = overrideItem.split(OVERRIDES_ID_DELIM)[1];

            if (overrideName.equals(OVERRIDE_UP_DISTANCE)) {
                mDownstreamDistance[FS_UP] = Integer.parseInt(overrideData);
            } else if (overrideName.equals(OVERRIDE_DOWN_DISTANCE)) {
                mDownstreamDistance[FS_DOWN] = Integer.parseInt(overrideData);
            } else if (overrideName.equals(OVERRIDE_ALTS)) {
                final String[] altItems = overrideData.split(ITEM_DELIM);
                int index = 0;
                while (index < altItems.length) {
                    if (!altItems[index].equals(ALT_DATA) || altItems.length - index < 4) {
                        return;
                    }

                    ++index;

                    mThreeGeneAltRegions.add(new ChrBaseRegion(altItems[index],
                            Integer.parseInt(altItems[index + 1]),
                            Integer.parseInt(altItems[index + 2])));
                    index += 3;
                }
            } else if (overrideName.equals(OVERRIDE_IG_RANGE)) {
                final String[] igRangeItems = overrideData.split(ITEM_DELIM);
                mIgStrand = Byte.parseByte(igRangeItems[0]);
                mIgRegion = new ChrBaseRegion(igRangeItems[1], Integer.parseInt(igRangeItems[2]), Integer.parseInt(igRangeItems[3]));
            }
        }
    }

    public boolean isHighImpactPromiscuous() {
        return mHighImpactPromiscuous;
    }

    public void setHighImpactPromiscuous() {
        mHighImpactPromiscuous = true;
    }

    public ChrBaseRegion igRegion() {
        return mIgRegion;
    }

    public String toString() {
        return String.format("%s: genes(%s - %s) ct(%s)", Type, FiveGene, ThreeGene, CancerTypes);
    }
}
