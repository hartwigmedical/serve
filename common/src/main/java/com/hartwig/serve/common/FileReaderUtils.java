package com.hartwig.serve.common;

import java.util.Map;

import com.google.common.collect.Maps;

import org.jetbrains.annotations.NotNull;

public final class FileReaderUtils {

    private FileReaderUtils() {
    }

    @NotNull
    public static Map<String, Integer> createFields(@NotNull String fieldsHeader, @NotNull String delimiter) {
        final String[] items = fieldsHeader.split(delimiter, -1);
        final Map<String, Integer> fieldsIndexMap = Maps.newLinkedHashMap();

        for (int i = 0; i < items.length; ++i) {
            fieldsIndexMap.put(items[i], i);
        }

        return fieldsIndexMap;
    }
}
