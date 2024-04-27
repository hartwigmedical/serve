package com.hartwig.serve.sources.ckb.blacklist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
                "Basket of Baskets",
                null,
                null,
                "KRAS",
                null);
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets",
                "Nivolumab",
                "Solid tumor",
                "BRAF",
                "amplification",
                blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Nivolumab", "Solid tumor", "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistAllStudiesBasedOnGeneAndEvent() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                createBlacklistStudyEntryList(CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE_AND_EVENT,
                        "Basket of Baskets",
                        null,
                        null,
                        "KRAS",
                        "amplification");
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistWholeStudy() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                createBlacklistStudyEntryList(CkbBlacklistStudyType.STUDY_WHOLE, "Basket of Baskets", null, null, null, null);
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("DRUP", "Nivolumab", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("DRUP", "Nivolumab", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapy() {
        CkbBlacklistStudyEntry blacklistStudyEntry = createBlacklistStudyEntryList(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY,
                "Basket of Baskets",
                "Nivolumab",
                null,
                null,
                null);
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets", "Chemo", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Chemo", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerType() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                createBlacklistStudyEntryList(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE,
                        "Basket of Baskets",
                        "Nivolumab",
                        "Solid tumor",
                        null,
                        null);
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets", "Chemo", "Breast", "KRAS", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Chemo", "Breast", "KRAS", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerTypeAndGene() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                createBlacklistStudyEntryList(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                        "Basket of Baskets",
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        null);
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets", "Chemo", "Breast", "PTEN", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Chemo", "Breast", "PTEN", "deletion"));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerTypeAndGeneAndEvent() {
        CkbBlacklistStudyEntry blacklistStudyEntry =
                createBlacklistStudyEntryList(CkbBlacklistStudyType.STUDY_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                        "Basket of Baskets",
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        "amplification");
        CkbStudyBlacklistModel ckbBlacklistStudy = createCkbBlacklistStudy(blacklistStudyEntry);

        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets", "Chemo", "Breast", "PTEN", "deletion", blacklistStudyEntry));
        assertTrue(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(ckbBlacklistStudy.isBlacklistStudy("Basket of Baskets", "Chemo", "Breast", "PTEN", "deletion"));
    }
}