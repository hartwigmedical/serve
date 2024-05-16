package com.hartwig.serve.sources.ckb.blacklist;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;

import org.jetbrains.annotations.NotNull;

public final class CkbBlacklistTestFactory {

    private CkbBlacklistTestFactory() {
    }

    @NotNull
    public static CkbEvidenceBlacklistModel createEmptyEvidenceBlacklist() {
        return new CkbEvidenceBlacklistModel(Lists.newArrayList());
    }

    @NotNull
    public static CkbEvidenceBlacklistModel createSpecificEvidenceBlacklist(@NotNull CkbBlacklistEvidenceEntry entry) {
        return new CkbEvidenceBlacklistModel(Lists.newArrayList(entry));
    }

    @NotNull
    public static CkbEvidenceBlacklistModel createProperEvidenceBlacklist() {
        List<CkbBlacklistEvidenceEntry> blacklistEvidences = Lists.newArrayList();
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

        blacklistEvidences.add(entry1);
        blacklistEvidences.add(entry2);
        blacklistEvidences.add(entry3);
        return new CkbEvidenceBlacklistModel(blacklistEvidences);
    }

    @NotNull
    public static CkbStudyBlacklistModel createEmptyStudyBlacklist() {
        return new CkbStudyBlacklistModel(Lists.newArrayList());
    }

    @NotNull
    public static CkbStudyBlacklistModel createSpecificStudyBlacklist(@NotNull CkbBlacklistStudyEntry blacklistStudyEntry) {
        return new CkbStudyBlacklistModel(Lists.newArrayList(blacklistStudyEntry));
    }

    @NotNull
    public static CkbStudyBlacklistModel createProperStudyBlacklist() {
        List<CkbBlacklistStudyEntry> blacklistStudies = Lists.newArrayList();
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

        blacklistStudies.add(entry1);
        blacklistStudies.add(entry2);

        return new CkbStudyBlacklistModel(blacklistStudies);
    }
}