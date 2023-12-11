package com.hartwig.serve.sources.ckb;

import java.util.Set;

import com.hartwig.serve.ckb.datamodel.CkbEntry;

import org.jetbrains.annotations.NotNull;

interface ActionableEntryFactory {

    @NotNull
    Set<ActionableEntry> create(@NotNull CkbEntry entry, @NotNull String sourceEvent, @NotNull String sourceGene);
}
