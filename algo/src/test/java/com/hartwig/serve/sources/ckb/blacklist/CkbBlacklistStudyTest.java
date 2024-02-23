package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class CkbBlacklistStudyTest {


    @NotNull
    public static CkbBlacklistStudy createCkbBlacklistStudy() {
        return new CkbBlacklistStudy(Lists.newArrayList());
    }

    @NotNull
    private static CkbBlacklistStudyEntry createBlacklistStudyEntryList(@NotNull CkbBlacklistStudyReason reason, @NotNull String nctId, @Nullable String therapy, @Nullable String cancerType, @Nullable String gene, @Nullable String event) {
        return ImmutableCkbBlacklistStudyEntry.builder().ckbBlacklistReason(reason).nctId(nctId).therapy(therapy).cancerType(cancerType).gene(gene).event(event).build();
    }

    @Test
    public void canBlacklistAllStudiesBasedOnGene() {
        CkbBlacklistStudy ckbBlacklistStudy = createCkbBlacklistStudy();
        CkbBlacklistStudyEntry blacklistStudyEntry = createBlacklistStudyEntryList(CkbBlacklistStudyReason.ALL_STUDIES_BASED_ON_GENE, "Basket of Baskets", null, null, "KRAS", null);
        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets", "Nivolumab", "Solid tumor", "BRAF", "amplification", blacklistStudyEntry));
    }

    @Test
    public void canBlacklistAllStudiesBasedOnGeneAndEvent() {
        CkbBlacklistStudy ckbBlacklistStudy = createCkbBlacklistStudy();
        CkbBlacklistStudyEntry blacklistStudyEntry = createBlacklistStudyEntryList(CkbBlacklistStudyReason.ALL_STUDIES_BASED_ON_GENE_AND_EVENT, "Basket of Baskets", null, null, "KRAS", "amplification");
        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
    }

    @Test
    public void canBlacklistWholeStudy() {
        CkbBlacklistStudy ckbBlacklistStudy = createCkbBlacklistStudy();
        CkbBlacklistStudyEntry blacklistStudyEntry = createBlacklistStudyEntryList(CkbBlacklistStudyReason.STUDY_WHOLE, "Basket of Baskets", null, null, null, null);
        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("DRUP", "Nivolumab", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapy() {
        CkbBlacklistStudy ckbBlacklistStudy = createCkbBlacklistStudy();
        CkbBlacklistStudyEntry blacklistStudyEntry = createBlacklistStudyEntryList(CkbBlacklistStudyReason.STUDY_BASED_ON_THERAPY, "Basket of Baskets", "Nivolumab", null, null, null);
        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets", "Chemo", "Solid tumor", "KRAS", "deletion", blacklistStudyEntry));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerType() {
        CkbBlacklistStudy ckbBlacklistStudy = createCkbBlacklistStudy();
        CkbBlacklistStudyEntry blacklistStudyEntry = createBlacklistStudyEntryList(CkbBlacklistStudyReason.STUDY_BASED_ON_THERAPY, "Basket of Baskets", "Nivolumab", "Solid tumor", null, null);
        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets", "Chemo", "Breast", "KRAS", "deletion", blacklistStudyEntry));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerTypeAndGene() {
        CkbBlacklistStudy ckbBlacklistStudy = createCkbBlacklistStudy();
        CkbBlacklistStudyEntry blacklistStudyEntry = createBlacklistStudyEntryList(CkbBlacklistStudyReason.STUDY_BASED_ON_THERAPY, "Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", null);
        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets", "Chemo", "Breast", "PTEN", "deletion", blacklistStudyEntry));
    }

    @Test
    public void canBlacklistStudyBasedOnTherapyAndCancerTypeAndGeneAndEvent() {
        CkbBlacklistStudy ckbBlacklistStudy = createCkbBlacklistStudy();
        CkbBlacklistStudyEntry blacklistStudyEntry = createBlacklistStudyEntryList(CkbBlacklistStudyReason.STUDY_BASED_ON_THERAPY, "Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification");
        assertTrue(ckbBlacklistStudy.isMatch("Basket of Baskets", "Nivolumab", "Solid tumor", "KRAS", "amplification", blacklistStudyEntry));
        assertFalse(ckbBlacklistStudy.isMatch("Basket of Baskets", "Chemo", "Breast", "PTEN", "deletion", blacklistStudyEntry));
    }
}