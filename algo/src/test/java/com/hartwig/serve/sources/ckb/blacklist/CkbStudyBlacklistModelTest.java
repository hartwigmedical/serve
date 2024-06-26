package com.hartwig.serve.sources.ckb.blacklist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbStudyBlacklistModelTest {

    @Test
    public void canBlacklistAllStudiesBasedOnGene() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                create(CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE, "NCT1", null, null, "KRAS", null);
        CkbStudyBlacklistModel ckbBlacklistStudy = CkbBlacklistTestFactory.createSpecificStudyBlacklist(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1", "Nivolumab", "Solid tumor", "BRAF", "amplification", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistAllStudiesBasedOnGeneAndEvent() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                create(CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE_AND_EVENT, "NCT1", null, null, "KRAS", "amplification");
        CkbStudyBlacklistModel ckbBlacklistStudy = CkbBlacklistTestFactory.createSpecificStudyBlacklist(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistWholeStudy() {
        CkbBlacklistStudyEntry blacklistStudyEntry = create(CkbBlacklistStudyType.STUDY_WHOLE, "NCT1", null, null, null, null);
        CkbStudyBlacklistModel ckbBlacklistStudy = CkbBlacklistTestFactory.createSpecificStudyBlacklist(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT2", "Nivolumab", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT2", "Nivolumab", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapy() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                create(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY, "NCT1", "Nivolumab", null, null, null);
        CkbStudyBlacklistModel ckbBlacklistStudy = CkbBlacklistTestFactory.createSpecificStudyBlacklist(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1", "Chemo", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Chemo", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerType() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                create(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE, "NCT1", "Nivolumab", "Solid tumor", null, null);
        CkbStudyBlacklistModel ckbBlacklistStudy = CkbBlacklistTestFactory.createSpecificStudyBlacklist(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1", "Chemo", "Breast", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Chemo", "Breast", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerTypeAndGene() {
        CkbBlacklistStudyEntry blacklistStudyEntry = create(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                "NCT1",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                null);
        CkbStudyBlacklistModel ckbBlacklistStudy = CkbBlacklistTestFactory.createSpecificStudyBlacklist(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1", "Chemo", "Breast", "PTEN", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Chemo", "Breast", "PTEN", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerTypeAndGeneAndEvent() {
        CkbBlacklistStudyEntry blacklistStudyEntry = create(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                "NCT1",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification");
        CkbStudyBlacklistModel ckbBlacklistStudy = CkbBlacklistTestFactory.createSpecificStudyBlacklist(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("NCT1", "Chemo", "Breast", "PTEN", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("NCT1", "Chemo", "Breast", "PTEN", "deletion"));
    }

    @NotNull
    private static CkbBlacklistStudyEntry create(@NotNull CkbBlacklistStudyType type, @NotNull String nctId, @Nullable String therapy,
            @Nullable String cancerType, @Nullable String gene, @Nullable String event) {
        return ImmutableCkbBlacklistStudyEntry.builder()
                .type(type)
                .nctId(nctId)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .build();
    }
}