package com.hartwig.serve.sources.ckb.blacklist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.hartwig.serve.datamodel.EvidenceLevel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbEvidenceBlacklistModelTest {

    @Test
    public void canBlacklistAllEvidenceBasedOnGene() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                create(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE, "Nivolumab", null, "KRAS", null, null);
        CkbEvidenceBlacklistModel blacklistEvidence = CkbBlacklistTestFactory.createSpecificEvidenceBlacklist(blacklistEvidenceEntry);

        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntry));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel =
                create(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE, "Nivolumab", null, "KRAS", null, EvidenceLevel.C);
        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.C,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification"));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistAllEvidenceBasedOnGeneAndEvent() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                create(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT, "Nivolumab", null, "KRAS", "amplification", null);
        CkbEvidenceBlacklistModel blacklistEvidence = CkbBlacklistTestFactory.createSpecificEvidenceBlacklist(blacklistEvidenceEntry);

        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntry));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel = create(CkbBlacklistEvidenceType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT,
                "Nivolumab",
                null,
                "KRAS",
                "amplification",
                EvidenceLevel.C);
        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.C,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification"));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapy() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                create(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, null);
        CkbEvidenceBlacklistModel blacklistEvidence = CkbBlacklistTestFactory.createSpecificEvidenceBlacklist(blacklistEvidenceEntry);

        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(blacklistEvidence.isMatch("Chemo", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification", blacklistEvidenceEntry));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Chemo", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel =
                create(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, EvidenceLevel.B);
        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.B,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Chemo", "Solid tumor", EvidenceLevel.A, "BRAS", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapyAndCancerType() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                create(CkbBlacklistEvidenceType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE, "Nivolumab", "Solid tumor", null, null, null);
        CkbEvidenceBlacklistModel blacklistEvidence = CkbBlacklistTestFactory.createSpecificEvidenceBlacklist(blacklistEvidenceEntry);

        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(blacklistEvidence.isMatch("Nivolumab",
                "Colorectum",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntry));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Colorectum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel = create(CkbBlacklistEvidenceType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE,
                "Nivolumab",
                "Solid tumor",
                null,
                null,
                EvidenceLevel.A);
        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.B,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Colon", EvidenceLevel.B, "KRAS", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapyAndCancerTypeAndGene() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                create(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        null,
                        null);
        CkbEvidenceBlacklistModel blacklistEvidence = CkbBlacklistTestFactory.createSpecificEvidenceBlacklist(blacklistEvidenceEntry);

        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(blacklistEvidence.isMatch("Nivolumab",
                "Colorectum",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntry));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Colorectum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel =
                create(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        null,
                        EvidenceLevel.B);
        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.B,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapyAndCancerTypeAndGeneAndEvent() {
        CkbBlacklistEvidenceEntry blacklistEvidenceEntry =
                create(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        "amplification",
                        null);
        CkbEvidenceBlacklistModel blacklistEvidence = CkbBlacklistTestFactory.createSpecificEvidenceBlacklist(blacklistEvidenceEntry);

        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.A,
                "KRAS",
                "amplification",
                blacklistEvidenceEntry));
        assertFalse(blacklistEvidence.isMatch("Nivolumab",
                "Colorectum",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntry));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Colorectum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbBlacklistEvidenceEntry blacklistEvidenceEntryLevel =
                create(CkbBlacklistEvidenceType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                        "Nivolumab",
                        "Solid tumor",
                        "KRAS",
                        "amplification",
                        EvidenceLevel.D);
        assertTrue(blacklistEvidence.isMatch("Nivolumab",
                "Solid tumor",
                EvidenceLevel.D,
                "KRAS",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertFalse(blacklistEvidence.isMatch("Nivolumab",
                "Colorectum",
                EvidenceLevel.A,
                "BRAF",
                "amplification",
                blacklistEvidenceEntryLevel));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.D, "KRAS", "amplification"));
        assertTrue(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(blacklistEvidence.isBlacklistEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }

    @NotNull
    private static CkbBlacklistEvidenceEntry create(@NotNull CkbBlacklistEvidenceType type, @Nullable String therapy,
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