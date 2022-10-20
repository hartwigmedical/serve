package com.hartwig.serve.common.json;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JsonFunctions {

    private JsonFunctions() {
    }

    @Nullable
    public static JsonObject optionalObject(@NotNull JsonObject object, @NotNull String field) {
        return object.has(field) ? object(object, field) : null;
    }

    @Nullable
    public static JsonObject nullableObject(@NotNull JsonObject object, @NotNull String field) {
        return !isNull(object, field) ? object(object, field) : null;
    }

    @NotNull
    public static JsonObject object(@NotNull JsonObject object, @NotNull String field) {
        return object.getAsJsonObject(field);
    }

    @Nullable
    public static JsonArray optionalArray(@NotNull JsonObject object, @NotNull String field) {
        return object.has(field) ? array(object, field) : null;
    }

    @Nullable
    public static JsonArray nullableArray(@NotNull JsonObject object, @NotNull String field) {
        return !isNull(object, field) ? array(object, field) : null;
    }

    @NotNull
    public static JsonArray array(@NotNull JsonObject object, @NotNull String field) {
        return object.getAsJsonArray(field);
    }

    @Nullable
    public static List<String> optionalStringList(@NotNull JsonObject object, @NotNull String field) {
        return object.has(field) ? stringList(object, field) : null;
    }

    @Nullable
    public static List<String> nullableStringList(@NotNull JsonObject object, @NotNull String field) {
        return !isNull(object, field) ? stringList(object, field) : null;
    }

    @NotNull
    public static List<String> stringList(@NotNull JsonObject object, @NotNull String field) {
        List<String> values = Lists.newArrayList();
        if (object.get(field).isJsonPrimitive()) {
            values.add(string(object, field));
        } else {
            assert object.get(field).isJsonArray();
            for (JsonElement element : object.getAsJsonArray(field)) {
                values.add(element.getAsJsonPrimitive().getAsString());
            }
        }
        return values;
    }

    @Nullable
    public static String optionalNullableString(@NotNull JsonObject object, @NotNull String field) {
        return object.has(field) ? nullableString(object, field) : null;
    }

    @Nullable
    public static String optionalString(@NotNull JsonObject object, @NotNull String field) {
        return object.has(field) ?string(object, field) : null;
    }

    @Nullable
    public static String nullableString(@NotNull JsonObject object, @NotNull String field) {
        return !isNull(object, field) ? string(object, field) : null;
    }

    @NotNull
    public static String string(@NotNull JsonObject object, @NotNull String field) {
        return object.get(field).getAsJsonPrimitive().getAsString();
    }

    public static int integer(@NotNull JsonObject object, @NotNull String field) {
        return object.get(field).getAsJsonPrimitive().getAsInt();
    }

    private static boolean isNull(@NotNull JsonObject object, @NotNull String field) {
        return object.get(field).isJsonNull();
    }
}
