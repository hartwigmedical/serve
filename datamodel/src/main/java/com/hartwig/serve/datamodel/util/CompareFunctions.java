package com.hartwig.serve.datamodel.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CompareFunctions {

    private CompareFunctions() {
    }

    public static <T extends Comparable<T>> int compareSetOfComparable(@NotNull Set<T> set1, @NotNull Set<T> set2) {
        Iterator<T> set1Iterator = set1.iterator();
        Iterator<T> set2Iterator = set2.iterator();

        while (set1Iterator.hasNext() && set2Iterator.hasNext()) {
            int entryCompare = set1Iterator.next().compareTo(set2Iterator.next());
            if (entryCompare != 0) {
                return entryCompare;
            }
        }

        return Integer.compare(set2.size(), set1.size());
    }

    public static <T extends Comparable<T>> int compareSetOfSetOfComparable(@NotNull Set<Set<T>> set1, @NotNull Set<Set<T>> set2) {
        int sizeCompare = Integer.compare(set2.size(), set1.size());
        if (sizeCompare != 0) {
            return sizeCompare;
        }

        List<List<T>> sortedSet1 = set1.stream()
                .map(innerSet -> innerSet.stream().sorted().collect(Collectors.toList()))
                .sorted(CompareFunctions::compareListOfComparable)
                .collect(Collectors.toList());

        List<List<T>> sortedSet2 = set2.stream()
                .map(innerSet -> innerSet.stream().sorted().collect(Collectors.toList()))
                .sorted(CompareFunctions::compareListOfComparable)
                .collect(Collectors.toList());

        for (int i = 0; i < sortedSet1.size(); i++) {
            int entryCompare = compareListOfComparable(sortedSet1.get(i), sortedSet2.get(i));
            if (entryCompare != 0) {
                return entryCompare;
            }
        }

        return 0;
    }

    private static <T extends Comparable<T>> int compareListOfComparable(@NotNull List<T> list1, @NotNull List<T> list2) {
        Iterator<T> list1Iterator = list1.iterator();
        Iterator<T> list2Iterator = list2.iterator();

        while (list1Iterator.hasNext() && list2Iterator.hasNext()) {
            int entryCompare = list1Iterator.next().compareTo(list2Iterator.next());
            if (entryCompare != 0) {
                return entryCompare;
            }
        }

        return Integer.compare(list1.size(), list2.size());
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
