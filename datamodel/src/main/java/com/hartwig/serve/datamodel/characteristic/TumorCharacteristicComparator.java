package com.hartwig.serve.datamodel.characteristic;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TumorCharacteristicComparator implements Comparator<TumorCharacteristic> {

    @Override
    public int compare(@NotNull TumorCharacteristic characteristic1, @NotNull TumorCharacteristic characteristic2) {
        int typeCompare = characteristic1.type().toString().compareTo(characteristic2.type().toString());
        if (typeCompare != 0) {
            return typeCompare;
        }

        int cutoffCompare = CompareFunctions.compareNullableDoubles(characteristic1.cutoff(), characteristic2.cutoff());
        if (cutoffCompare != 0) {
            return cutoffCompare;
        }

        return compareCutoffTypes(characteristic1.cutoffType(), characteristic2.cutoffType());
    }

    private static int compareCutoffTypes(@Nullable TumorCharacteristicCutoffType type1, @Nullable TumorCharacteristicCutoffType type2) {
        if (type1 == null && type2 == null) {
            return 0;
        } else if (type1 == null) {
            return 1;
        } else if (type2 == null) {
            return -1;
        }

        return type1.toString().compareTo(type2.toString());
    }
}
