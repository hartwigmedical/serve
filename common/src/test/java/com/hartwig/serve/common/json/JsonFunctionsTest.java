package com.hartwig.serve.common.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.junit.Test;

public class JsonFunctionsTest {

    @Test
    public void canExtractObjects() {
        JsonObject object = new JsonObject();

        assertNull(JsonFunctions.optionalObject(object, "object"));

        object.add("object", new JsonObject());
        assertNotNull(JsonFunctions.optionalObject(object, "object"));
        assertNotNull(JsonFunctions.object(object, "object"));

        object.add("null", null);
        assertNull(JsonFunctions.nullableObject(object, "null"));
    }

    @Test
    public void canExtractArrays() {
        JsonObject object = new JsonObject();

        assertNull(JsonFunctions.optionalArray(object, "array1"));

        object.add("array1", new JsonArray());
        assertNotNull(JsonFunctions.nullableArray(object, "array1"));
        assertNotNull(JsonFunctions.optionalArray(object, "array1"));
        assertNotNull(JsonFunctions.array(object, "array1"));

        object.add("array2", JsonNull.INSTANCE);
        assertNull(JsonFunctions.nullableArray(object, "array2"));
    }

    @Test
    public void canExtractStringLists() {
        JsonObject object = new JsonObject();

        object.addProperty("nullable", (String) null);
        assertNull(JsonFunctions.nullableStringList(object, "nullable"));
        assertNull(JsonFunctions.optionalStringList(object, "array1"));

        JsonArray array = new JsonArray();
        array.add("value1");
        array.add("value2");
        object.add("array1", array);
        assertEquals(2, JsonFunctions.nullableStringList(object, "array1").size());
        assertEquals(2, JsonFunctions.optionalStringList(object, "array1").size());

        object.addProperty("string", "string");
        assertEquals(1, JsonFunctions.nullableStringList(object, "string").size());
    }

    @Test
    public void canExtractStrings() {
        JsonObject object = new JsonObject();

        assertNull(JsonFunctions.optionalString(object, "string"));

        object.addProperty("string", "value");
        assertEquals("value", JsonFunctions.nullableString(object, "string"));
        assertEquals("value", JsonFunctions.optionalString(object, "string"));

        object.addProperty("nullable", (String) null);
        assertNull(JsonFunctions.nullableString(object, "nullable"));
    }

    @Test
    public void canExtractIntegers() {
        JsonObject object = new JsonObject();

        object.addProperty("integer", 8);
        assertEquals(8, JsonFunctions.integer(object, "integer"));
    }
}