package com.hartwig.serve.sources.ckb.blacklist;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CkbBlacklistTestFactory {

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

    @NotNull
    public static CkbEvidenceBlacklistModel createCkbBlacklistEvidence() {
        List<CkbBlacklistEvidenceEntry> blacklistStudiesList = Lists.newArrayList();
        CkbBlacklistEvidenceEntry entry1 = ImmutableCkbBlacklistEvidenceEntry.builder()
                .type(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY)
                .therapy("Nivolumab")
                .cancerType(null)
                .gene(null)
                .event(null)
                .level(null)
                .build();

        CkbBlacklistEvidenceEntry entry2 = ImmutableCkbBlacklistEvidenceEntry.builder()
                .type(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE)
                .therapy(null)
                .cancerType(null)
                .gene("ATM")
                .event(null)
                .level(null)
                .build();

        CkbBlacklistEvidenceEntry entry3 = ImmutableCkbBlacklistEvidenceEntry.builder()
                .type(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY)
                .therapy("Immuno")
                .cancerType(null)
                .gene(null)
                .event(null)
                .level(EvidenceLevel.A)
                .build();

        blacklistStudiesList.add(entry1);
        blacklistStudiesList.add(entry2);
        blacklistStudiesList.add(entry3);
        return new CkbEvidenceBlacklistModel(blacklistStudiesList);
    }

    @NotNull
    public static CkbStudyBlacklistModel createCkbBlacklistStudies() {
        List<CkbBlacklistStudyEntry> blacklistStudiesList = Lists.newArrayList();
        CkbBlacklistStudyEntry entry1 = ImmutableCkbBlacklistStudyEntry.builder()
                .type(CkbBlacklistStudyType.STUDY_WHOLE)
                .nctId("NCT0456")
                .therapy(null)
                .cancerType(null)
                .gene(null)
                .event(null)
                .build();

        CkbBlacklistStudyEntry entry2 = ImmutableCkbBlacklistStudyEntry.builder()
                .type(CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE)
                .nctId(null)
                .therapy(null)
                .cancerType(null)
                .gene("EGFR")
                .event(null)
                .build();

        blacklistStudiesList.add(entry1);
        blacklistStudiesList.add(entry2);
        return new CkbStudyBlacklistModel(blacklistStudiesList);
    }
}