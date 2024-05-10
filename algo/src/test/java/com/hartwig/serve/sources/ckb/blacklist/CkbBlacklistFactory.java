package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbBlacklistFactory {

    @NotNull
    public static CkbEvidenceBlacklistModel createCkbBlacklistEvidenceEmpty() {
        return new CkbEvidenceBlacklistModel(Lists.newArrayList());
    }

    @NotNull
    public static CkbEvidenceBlacklistModel createCkbBlacklistEvidence(@NotNull CkbBlacklistEvidenceEntry entry) {
        return new CkbEvidenceBlacklistModel(Lists.newArrayList(entry));
    }

    @NotNull
    public static CkbBlacklistEvidenceEntry createBlacklistStudyEntryList(@NotNull CkbBlacklistEvidenceType type, @Nullable String therapy,
            @Nullable String cancerType, @Nullable String gene, @Nullable String event, @Nullable EvidenceLevel level) {
        return ImmutableCkbBlacklistEvidenceEntry.builder()
                .type(type)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .level(level)
                .build();
    }
}