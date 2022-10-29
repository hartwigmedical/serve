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
    public static String nullableBoolean(@Nullable Boolean bool) {
        return bool != null ? String.valueOf(bool) : Strings.EMPTY;
    }

    @NotNull
    public static String nullableNumber(@Nullable Double number) {
        return number != null ? String.valueOf(number) : Strings.EMPTY;
    }

    @NotNull
    public static String nullableInteger(@Nullable Integer integer) {
        return integer != null ? String.valueOf(integer) : Strings.EMPTY;
    }

    @NotNull
    public static String nullableString(@Nullable String string) {
        return string != null ? string : Strings.EMPTY;
    }

    @Nullable
    public static String optionalString(@NotNull String value) {
        return !value.isEmpty() ? value : null;
    }

    @Nullable
    public static Double optionalNumber(@NotNull String value) {
        return !value.isEmpty() ? Double.parseDouble(value) : null;
    }

    @Nullable
    public static Integer optionalInteger(@NotNull String value) {
        return !value.isEmpty() ? Integer.parseInt(value) : null;
    }

    @Nullable
    public static Boolean optionalBoolean(@NotNull String value) {
        return !value.isEmpty() ? Boolean.valueOf(value) : null;
    }
}
