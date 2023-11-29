package com.hartwig.serve.sources.ckb;

import java.util.List;

import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.extraction.EventExtractor;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ImmutableExtractionResult;

import org.jetbrains.annotations.NotNull;

public class CkbExtractor {

    @NotNull
    private final Knowledgebase source;
    @NotNull
    private final EventExtractor eventExtractor;
    @NotNull
    private final ActionableEntryFactory actionableEntryFactory;
    private final boolean generateKnownEvents;

    public CkbExtractor(@NotNull Knowledgebase source, @NotNull EventExtractor eventExtractor,
            @NotNull ActionableEntryFactory actionableEntryFactory, boolean generateKnownEvents) {
        this.source = source;
        this.eventExtractor = eventExtractor;
        this.actionableEntryFactory = actionableEntryFactory;
        this.generateKnownEvents = generateKnownEvents;
    }

    @NotNull
    public ExtractionResult extract(@NotNull List<CkbEntry> entries) {
        // TODO Implement
        return ImmutableExtractionResult.builder().refGenomeVersion(source.refGenomeVersion()).build();
    }
}
