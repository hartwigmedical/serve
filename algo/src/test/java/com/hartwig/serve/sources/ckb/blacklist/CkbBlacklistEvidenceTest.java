package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CkbBlacklistEvidenceTest {

    @NotNull
    public static CkbBlacklistEvidence createCkbBlacklistEvidenceEmpty() {
        return new CkbBlacklistEvidence(Lists.newArrayList());
    }

    @NotNull
    public static CkbBlacklistEvidence createCkbBlacklistEvidence(@NotNull CkbBlacklistEvidenceEntry entry) {
        return new CkbBlacklistEvidence(Lists.newArrayList(entry));
    }

    @NotNull
    private static CkbBlacklistEvidenceEntry createBlacklistStudyEntryList(@NotNull CkbBlacklistEvidenceReason reason, @Nullable String therapy, @Nullable String cancerType, @Nullable String gene, @Nullable String event, @Nullable EvidenceLevel level) {
        return ImmutableCkbBlacklistEvidenceEntry.builder().ckbBlacklistEvidenceReason(reason).therapy(therapy).cancerType(cancerType).gene(gene).event(event).level(level).build();
    }

    @Test
    public void canBlacklistAllEvidenceBasedOnGene() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.ALL_EVIDENCE_BASED_ON_GENE, "Nivolumab", null, "KRAS", null, null);
        CkbBlacklistEvidence ckbBlacklistEvidence = createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification", blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.ALL_EVIDENCE_BASED_ON_GENE, "Nivolumab", null, "KRAS", null, EvidenceLevel.C);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification", blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistAllEvidenceBasedOnGeneAndEvent() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT, "Nivolumab", null, "KRAS", "amplification", null);
        CkbBlacklistEvidence ckbBlacklistEvidence = createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification", blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT, "Nivolumab", null, "KRAS", "amplification", EvidenceLevel.C);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification", blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapy() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, null);
        CkbBlacklistEvidence ckbBlacklistEvidence = createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Chemo", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification", blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Chemo", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, EvidenceLevel.B);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification", blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Chemo", "Solid tumor", EvidenceLevel.A, "BRAS", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapyAndCancertype() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE, "Nivolumab", "Solid tumor", null, null, null);
        CkbBlacklistEvidence ckbBlacklistEvidence = createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab", "Colorectrum", EvidenceLevel.A, "BRAF", "amplification", blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Colorectrum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE, "Nivolumab", "Solid tumor", null, null, EvidenceLevel.A);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification", blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Colon", EvidenceLevel.B, "KRAS", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapyAndCancerTypeAndGene() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE, "Nivolumab", "Solid tumor", "KRAS", null, null);
        CkbBlacklistEvidence ckbBlacklistEvidence = createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab", "Colorectrum", EvidenceLevel.A, "BRAF", "amplification", blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Colorectrum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE, "Nivolumab", "Solid tumor", "KRAS", null, EvidenceLevel.B);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification", blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapyAndCancerTypeAndGeneAndEvent() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT, "Nivolumab", "Solid tumor", "KRAS", "amplification", null);
        CkbBlacklistEvidence ckbBlacklistEvidence = createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab", "Colorectrum", EvidenceLevel.A, "BRAF", "amplification", blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Colorectrum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel = createBlacklistStudyEntryList(CkbBlacklistEvidenceReason.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT, "Nivolumab", "Solid tumor", "KRAS", "amplification", EvidenceLevel.D);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.D, "KRAS", "amplification", blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab", "Colorectrum", EvidenceLevel.A, "BRAF", "amplification", blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.D, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }
}