package com.hartwig.serve.datamodel.serialization.util;

import java.util.Map;

import com.google.common.collect.Maps;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SerializationUtil {

    private SerializationUtil() {
    }

    @NotNull
    public static Map<String, Integer> createFields(@NotNull String header, @NotNull String delimiter) {
        String[] items = header.split(delimiter);
        Map<String, Integer> fieldsIndexMap = Maps.newHashMap();

        for (int i = 0; i < items.length; ++i) {
            fieldsIndexMap.put(items[i], i);
        }

        return fieldsIndexMap;
    }

    @NotNull
    public static String nullableNumber(@Nullable Double number) {
        return number != null ? String.valueOf(number) : Strings.EMPTY;
    }

    @NotNull
    public static String nullableInteger(@Nullable Integer integer) {
        return integer != null ? String.valueOf(integer) : Strings.EMPTY;
    }

    @Nullable
    public static String optionalString(@NotNull String string) {
        return !string.isEmpty() ? string : null;
    }

    @Nullable
    public static Integer optionalInteger(@NotNull String value) {
        return !value.isEmpty() ? Integer.parseInt(value) : null;
    }
}
