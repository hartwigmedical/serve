package com.hartwig.serve.datamodel.serialization.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class SerializationUtilTest {

    private static final double EPSILON = 1.0E-10;

    @Test
    public void canCreateFields() {
        String header = "field1\tfield2\tfield3";
        Map<String, Integer> fields = SerializationUtil.createFields(header, "\t");

        assertEquals(3, fields.size());
        assertEquals(0, (int) fields.get("field1"));
        assertEquals(1, (int) fields.get("field2"));
        assertEquals(2, (int) fields.get("field3"));
    }

    @Test
    public void canHandleNullableAndOptionalStrings() {
        assertEquals(Strings.EMPTY, SerializationUtil.nullableString(null));

        assertNull(SerializationUtil.optionalString(Strings.EMPTY));
        assertEquals("hi", SerializationUtil.optionalString("hi"));
    }

    @Test
    public void canHandleNullableAndOptionalIntegers() {
        assertEquals(Strings.EMPTY, SerializationUtil.nullableInteger(null));
        assertEquals("2", SerializationUtil.nullableInteger(2));

        assertNull(SerializationUtil.optionalInteger(Strings.EMPTY));
        assertEquals(3, (int) SerializationUtil.optionalInteger("3"));
    }

    @Test
    public void canHandleNullableAndOptionalNumbers() {
        assertEquals(Strings.EMPTY, SerializationUtil.nullableNumber(null));
        assertEquals("3.0", SerializationUtil.nullableNumber(3D));

        assertNull(SerializationUtil.optionalNumber(Strings.EMPTY));
        assertEquals(3, SerializationUtil.optionalNumber("3"), EPSILON);
    }

    @Test
    public void canHandleNullableAndOptionalBooleans() {
        assertEquals(Strings.EMPTY, SerializationUtil.nullableBoolean(null));
        assertEquals("true", SerializationUtil.nullableBoolean(true));
        assertEquals("false", SerializationUtil.nullableBoolean(false));

        assertNull(SerializationUtil.optionalBoolean(Strings.EMPTY));
        assertTrue(SerializationUtil.optionalBoolean("true"));
        assertFalse(SerializationUtil.optionalBoolean("false"));
    }
}