package com.hartwig.serve.datamodel.characteristic;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TumorCharacteristicComparator implements Comparator<TumorCharacteristic> {

    @Override
    public int compare(@NotNull TumorCharacteristic characteristic1, @NotNull TumorCharacteristic characteristic2) {
        int nameCompare = characteristic1.type().toString().compareTo(characteristic2.type().toString());
        if (nameCompare != 0) {
            return nameCompare;
        }

        int cutoffCompare = compareDoubles(characteristic1.cutoff(), characteristic2.cutoff());
        if (cutoffCompare != 0) {
            return cutoffCompare;
        }

        return compareCutoffTypes(characteristic1.cutoffType(), characteristic2.cutoffType());
    }

    private static int compareDoubles(@Nullable Double double1, @Nullable Double double2) {
        if (double1 == null && double2 == null) {
            return 0;
        } else if (double1 == null) {
            return 1;
        } else if (double2 == null) {
            return -1;
        }

        return double1.compareTo(double2);
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
