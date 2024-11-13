package com.hartwig.serve.datamodel.molecular.fusion;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class KnownFusionComparator implements Comparator<KnownFusion> {

    @NotNull
    private final Comparator<FusionPair> fusionPairComparator = new FusionPairComparator();

    @Override
    public int compare(@NotNull KnownFusion fusion1, @NotNull KnownFusion fusion2) {
        int fusionPairCompare = fusionPairComparator.compare(fusion1, fusion2);
        if (fusionPairCompare != 0) {
            return fusionPairCompare;
        }

        int proteinEffectCompare = fusion1.proteinEffect().toString().compareTo(fusion2.proteinEffect().toString());
        if (proteinEffectCompare != 0) {
            return proteinEffectCompare;
        }

        return CompareFunctions.compareNullableBoolean(fusion1.associatedWithDrugResistance(), fusion2.associatedWithDrugResistance());
    }
}
