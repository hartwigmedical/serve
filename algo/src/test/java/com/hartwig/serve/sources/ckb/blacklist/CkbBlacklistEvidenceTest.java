package com.hartwig.serve.sources.ckb.blacklist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.EvidenceLevel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbBlacklistEvidenceTest {


    @NotNull
    public static CkbEvidenceBlacklistModel defineEvidenceModel(@NotNull CkbBlacklistEvidenceType type, @Nullable String therapy,
            @Nullable String cancerType, @Nullable String gene, @Nullable String event, @Nullable EvidenceLevel level) {
        List<CkbBlacklistEvidenceEntry> blacklistStudiesList = Lists.newArrayList();
        CkbBlacklistEvidenceEntry entry1 = ImmutableCkbBlacklistEvidenceEntry.builder()
                .type(type)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .level(level)
                .build();

        blacklistStudiesList.add(entry1);
        return new CkbEvidenceBlacklistModel(blacklistStudiesList);
    }


    @Test
    public void canBlacklistAllEvidenceBasedOnGene() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE, "Nivolumab", null, "KRAS", null, null);
        CkbEvidenceBlacklistModel ckbBlacklistEvidence = CkbBlacklistTestFactory.createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE,
                        "Nivolumab",
                        null,
                        "KRAS",
                        null,
                        EvidenceLevel.C);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.C,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistAllEvidenceBasedOnGeneAndEvent() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT,
                        "Nivolumab",
                        null,
                        "KRAS",
                        "amplification",
                        null);
        CkbEvidenceBlacklistModel ckbBlacklistEvidence = CkbBlacklistTestFactory.createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT,
                        "Nivolumab",
                        null,
                        "KRAS",
                        "amplification",
                        EvidenceLevel.C);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.C,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapy() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, null);
        CkbEvidenceBlacklistModel ckbBlacklistEvidence = CkbBlacklistTestFactory.createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Chemo", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification", blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Chemo", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY,
                        "Nivolumab",
                        null,
                        null,
                        null,
                        EvidenceLevel.B);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.B,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Chemo", "Solid tumor", EvidenceLevel.A, "BRAS", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapyAndCancertype() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
                        "Nivolumab",
                        "Solid tumor",
                        null,
                        null,
                        null);
        CkbEvidenceBlacklistModel ckbBlacklistEvidence = CkbBlacklistTestFactory.createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Colorectrum",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Colorectrum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
                        "Nivolumab",
                        "Solid tumor",
                        null,
                        null,
                        EvidenceLevel.A);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.B,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Colon", EvidenceLevel.B, "KRAS", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapyAndCancerTypeAndGene() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        null,
                        null);
        CkbEvidenceBlacklistModel ckbBlacklistEvidence = CkbBlacklistTestFactory.createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Colorectrum",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Colorectrum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        null,
                        EvidenceLevel.B);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.B,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapyAndCancerTypeAndGeneAndEvent() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        "amplification",
                        null);
        CkbEvidenceBlacklistModel ckbBlacklistEvidence = CkbBlacklistTestFactory.createCkbBlacklistEvidence(blacklistEvidenceEntry);

        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Colorectrum",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntry));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Colorectrum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel =
                CkbBlacklistTestFactory.createBlacklistStudyEntryList(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        "amplification",
                        EvidenceLevel.D);
        assertTrue(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.D,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(ckbBlacklistEvidence.isMatch("Nivolumab",
                "Colorectrum",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.D, "KRAS", "amplification"));
        assertTrue(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(ckbBlacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }
}