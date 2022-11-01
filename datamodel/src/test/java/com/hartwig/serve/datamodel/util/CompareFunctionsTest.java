package com.hartwig.serve.datamodel.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CompareFunctionsTest {

    @Test
    public void canCompareNullableObjects() {
        assertEquals(0, CompareFunctions.compareNullableDoubles(null, null));
        assertEquals(0, CompareFunctions.compareNullableIntegers(null, null));
        assertEquals(0, CompareFunctions.compareNullableBoolean(null, null));
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