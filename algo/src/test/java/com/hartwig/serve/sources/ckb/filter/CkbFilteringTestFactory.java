package com.hartwig.serve.sources.ckb.filter;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.sources.ckb.blacklist.ImmutableCkbEvidenceFilterEntry;
import com.hartwig.serve.sources.ckb.blacklist.ImmutableCkbTrialFilterEntry;

import org.jetbrains.annotations.NotNull;

public final class CkbFilteringTestFactory {

    private CkbFilteringTestFactory() {
    }

    @NotNull
    public static CkbEvidenceFilterModel createEmptyEvidenceFilterModel() {
        return new CkbEvidenceFilterModel(Lists.newArrayList());
    }

    @NotNull
    public static CkbEvidenceFilterModel createSpecificEvidenceFilterModel(@NotNull CkbEvidenceFilterEntry entry) {
        return new CkbEvidenceFilterModel(Lists.newArrayList(entry));
    }

    @NotNull
    public static CkbEvidenceFilterModel createProperEvidenceFilterModel() {
        CkbEvidenceFilterEntry entry1 = ImmutableCkbEvidenceFilterEntry.builder()
                .type(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY)
                .therapy("Nivolumab")
                .cancerType(null)
                .gene(null)
                .event(null)
                .level(null)
                .build();

        CkbEvidenceFilterEntry entry2 = ImmutableCkbEvidenceFilterEntry.builder()
                .type(CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE)
                .therapy(null)
                .cancerType(null)
                .gene("ATM")
                .event(null)
                .level(null)
                .build();

        CkbEvidenceFilterEntry entry3 = ImmutableCkbEvidenceFilterEntry.builder()
                .type(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY)
                .therapy("Immuno")
                .cancerType(null)
                .gene(null)
                .event(null)
                .level(EvidenceLevel.A)
                .build();

        return new CkbEvidenceFilterModel(Lists.newArrayList(entry1, entry2, entry3));
    }

    @NotNull
    public static CkbTrialFilterModel createEmptyTrialFilterModel() {
        return new CkbTrialFilterModel(Lists.newArrayList());
    }

    @NotNull
    public static CkbTrialFilterModel createSpecificTrialFilterModel(@NotNull CkbTrialFilterEntry entry) {
        return new CkbTrialFilterModel(Lists.newArrayList(entry));
    }

    @NotNull
    public static CkbTrialFilterModel createProperTrialFilterModel() {
        CkbTrialFilterEntry entry1 = ImmutableCkbTrialFilterEntry.builder()
                .type(CkbTrialFilterType.COMPLETE_TRIAL)
                .nctId("NCT0456")
                .therapy(null)
                .cancerType(null)
                .gene(null)
                .event(null)
                .build();

        CkbTrialFilterEntry entry2 = ImmutableCkbTrialFilterEntry.builder()
                .type(CkbTrialFilterType.ALL_TRIALS_BASED_ON_GENE)
                .nctId(null)
                .therapy(null)
                .cancerType(null)
                .gene("EGFR")
                .event(null)
                .build();

        return new CkbTrialFilterModel(Lists.newArrayList(entry1, entry2));
    }
}