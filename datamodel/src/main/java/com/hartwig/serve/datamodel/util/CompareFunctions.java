package com.hartwig.serve.datamodel.util;

import com.google.common.annotations.VisibleForTesting;

import org.jetbrains.annotations.Nullable;

public final class CompareFunctions {

    private CompareFunctions() {
    }

    public static int compareNullableDoubles(@Nullable Double double1, @Nullable Double double2) {
        return compareNullable(double1, double2);
    }

    public static int compareNullableBoolean(@Nullable Boolean bool1, @Nullable Boolean bool2) {
        return compareNullable(bool1, bool2);
    }

    public static int compareNullableIntegers(@Nullable Integer int1, @Nullable Integer int2) {
        return compareNullable(int1, int2);
    }

    @VisibleForTesting
    static <T extends Comparable<T>> int compareNullable(@Nullable T int1, @Nullable T int2) {
        if (int1 == null && int2 == null) {
            return 0;
        } else if (int1 == null) {
            return 1;
        } else if (int2 == null) {
            return -1;
        }

        return int1.compareTo(int2);
    }
}
