package com.hartwig.serve.datamodel.util;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

public class CompareFunctionsTest {

    @Test
    public void canCompareSetsOfStrings() {
        assertEquals(1, CompareFunctions.compareSetOfStrings(Set.of(), Set.of("string")));
        assertEquals(0, CompareFunctions.compareSetOfStrings(Set.of("string"), Set.of("string")));
        assertEquals(-1, CompareFunctions.compareSetOfStrings(Set.of("string"), Set.of()));
    }

    @Test
    public void canCompareNullableObjects() {
        assertEquals(0, CompareFunctions.compareNullableStrings(null, null));
        assertEquals(0, CompareFunctions.compareNullableBoolean(null, null));
        assertEquals(0, CompareFunctions.compareNullableDoubles(null, null));
        assertEquals(0, CompareFunctions.compareNullableIntegers(null, null));
    }

    @Test
    public void canCompareNullable() {
        assertEquals(1, CompareFunctions.compareNullable(null, 1));
        assertEquals(0, CompareFunctions.compareNullable(null, null));
        assertEquals(-1, CompareFunctions.compareNullable(1, null));

        assertEquals(1, CompareFunctions.compareNullable(2, 1));
        assertEquals(0, CompareFunctions.compareNullable(2, 2));
        assertEquals(-1, CompareFunctions.compareNullable(1, 2));
    }
}