package com.hartwig.serve.sources.ckb.blacklist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbBlacklistStudyTest {

    @NotNull
    public static CkbStudyBlacklistModel createCkbBlacklistStudyEmpty() {
        return new CkbStudyBlacklistModel(Lists.newArrayList());
    }

    @NotNull
    public static CkbStudyBlacklistModel defineBlacklistStudies(@NotNull CkbBlacklistStudyType type, @Nullable String nctId, @Nullable String therapy,
            @Nullable String cancerType, @Nullable String gene, @Nullable String event) {
        List<CkbBlacklistStudyEntry> blacklistStudiesList = Lists.newArrayList();
        CkbBlacklistStudyEntry entry1 = ImmutableCkbBlacklistStudyEntry.builder()
                .type(type)
                .nctId(nctId)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .build();

        blacklistStudiesList.add(entry1);
        return new CkbStudyBlacklistModel(blacklistStudiesList);
    }



    @NotNull
    public static CkbStudyBlacklistModel createCkbBlacklistStudy(@NotNull CkbBlacklistStudyEntry blacklistStudyEntry) {
        return new CkbStudyBlacklistModel(Lists.newArrayList(blacklistStudyEntry));
    }

    @NotNull
    private static CkbBlacklistStudyEntry createBlacklistStudyEntryList(@NotNull CkbBlacklistStudyType type, @NotNull String nctId,
            @Nullable String therapy, @Nullable String cancerType, @Nullable String gene, @Nullable String event) {
        return ImmutableCkbBlacklistStudyEntry.builder()
                .type(type)
                .nctId(nctId)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .build();
    }

    @Test
    public void canBlacklistAllStudiesBasedOnGene() {
        CkbBlacklistStudyEntry blacklistStudyEntry = createBlacklistStudyEntryList(CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE,
                "NCT1",
                null,
                null,
                "KRAS",
                null);
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1",
                "Nivolumab",
                "Solid tumor",
                "BRAF",
                "amplification",
                blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistAllStudiesBasedOnGeneAndEvent() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                createBlacklistStudyEntryList(CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE_AND_EVENT,
                        "NCT1",
                        null,
                        null,
                        "KRAS",
                        "amplification");
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistWholeStudy() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                createBlacklistStudyEntryList(CkbBlacklistStudyType.STUDY_WHOLE, "NCT1", null, null, null, null);
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT2", "Nivolumab", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT2", "Nivolumab", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapy() {
        CkbBlacklistStudyEntry blacklistStudyEntry = createBlacklistStudyEntryList(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY,
                "NCT1",
                "Nivolumab",
                null,
                null,
                null);
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1", "Chemo", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Chemo", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerType() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                createBlacklistStudyEntryList(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE,
                        "NCT1",
                        "Nivolumab",
                        "Solid tumor",
                        null,
                        null);
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1", "Chemo", "Breast", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Chemo", "Breast", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerTypeAndGene() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                createBlacklistStudyEntryList(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                        "NCT1",
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        null);
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1", "Chemo", "Breast", "PTEN", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Chemo", "Breast", "PTEN", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerTypeAndGeneAndEvent() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                createBlacklistStudyEntryList(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                        "NCT1",
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        "amplification");
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1", "Chemo", "Breast", "PTEN", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Chemo", "Breast", "PTEN", "deletion"));
    }
}