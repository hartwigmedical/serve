package com.hartwig.serve.vicc.util;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hartwig.serve.common.json.Json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ViccJson {

    // These functions don't exist in the common Json class because they should not be necessary in a proper json datamodel.
    // VICC json is not very well-structured though (objects can both be missing and null).

    private ViccJson() {
    }

    @Nullable
    public static JsonObject optionalOrNullableObject(@NotNull JsonObject object, @NotNull String field) {
        return object.has(field) ? Json.nullableObject(object, field) : null;
    }

    @Nullable
    public static JsonArray optionalOrNullableArray(@NotNull JsonObject object, @NotNull String field) {
        return object.has(field) ? Json.nullableArray(object, field) : null;
    }

    @NotNull
    public static List<String> optionalStringList(@NotNull JsonObject object, @NotNull String field) {
        if (!object.has(field)) {
            return Lists.newArrayList();
        }

        List<String> strings = Json.nullableStringList(object, field);
        return strings != null ? strings : Lists.newArrayList();
    }

    @Nullable
    public static String optionalOrNullableString(@NotNull JsonObject object, @NotNull String field) {
        return object.has(field) ? Json.nullableString(object, field) : null;
    }
}
