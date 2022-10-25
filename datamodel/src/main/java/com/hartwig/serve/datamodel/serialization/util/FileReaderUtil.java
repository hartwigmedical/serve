package com.hartwig.serve.datamodel.serialization.util;

import java.util.Map;

import com.google.common.collect.Maps;

import org.jetbrains.annotations.NotNull;

public final class FileReaderUtil {

    private FileReaderUtil() {
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
}
