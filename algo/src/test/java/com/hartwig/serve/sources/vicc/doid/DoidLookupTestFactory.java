package com.hartwig.serve.sources.vicc.doid;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import org.jetbrains.annotations.NotNull;

public final class DoidLookupTestFactory {

    private DoidLookupTestFactory() {
    }

    @NotNull
    public static DoidLookup dummy() {
        return create(Maps.newHashMap());
    }

    @NotNull
    public static DoidLookup create(@NotNull Map<String, Set<String>> mappings) {
        return new DoidLookup(mappings);
    }
}
