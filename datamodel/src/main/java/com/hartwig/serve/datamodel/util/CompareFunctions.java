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
        Iterator<String> set1Iterator = set1.iterator();
        Iterator<String> set2Iterator = set2.iterator();

        while (set1Iterator.hasNext() && set2Iterator.hasNext()) {
            int stringCompare = set1Iterator.next().compareTo(set2Iterator.next());
            if (stringCompare != 0) {
                return stringCompare;
            }
        }

        return Integer.compare(set2.size(), set1.size());
    }

    public static int compareNullableStrings(@Nullable String string1, @Nullable String string2) {
        return compareNullable(string1, string2);
    }

    public static int compareNullableBoolean(@Nullable Boolean bool1, @Nullable Boolean bool2) {
        return compareNullable(bool1, bool2);
    }

    public static int compareNullableDoubles(@Nullable Double double1, @Nullable Double double2) {
        return compareNullable(double1, double2);
    }

    public static int compareNullableIntegers(@Nullable Integer int1, @Nullable Integer int2) {
        return compareNullable(int1, int2);
    }

    @VisibleForTesting
    static <T extends Comparable<T>> int compareNullable(@Nullable T obj1, @Nullable T obj2) {
        if (obj1 == null && obj2 == null) {
            return 0;
        } else if (obj1 == null) {
            return 1;
        } else if (obj2 == null) {
            return -1;
        }

        return obj1.compareTo(obj2);
    }
}
