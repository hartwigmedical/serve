package com.hartwig.serve.datamodel.util;

import java.util.Iterator;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CompareFunctions {

    private CompareFunctions() {
    }

    public static int compareSetOfStrings(@NotNull Set<String> set1, @NotNull Set<String> set2) {
        int sizeCompare = Integer.compare(set2.size(), set1.size());
        if (sizeCompare != 0) {
            return sizeCompare;
        }

        Iterator<String> set1Iterator = set1.iterator();
        Iterator<String> set2Iterator = set2.iterator();
        while (set1Iterator.hasNext()) {
            assert set2Iterator.hasNext();
            String set1Entry = set1Iterator.next();
            String set2Entry = set2Iterator.next();
            int stringCompare = set1Entry.compareTo(set2Entry);
            if (stringCompare != 0) {
                return stringCompare;
            }
        }
        return 0;
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
