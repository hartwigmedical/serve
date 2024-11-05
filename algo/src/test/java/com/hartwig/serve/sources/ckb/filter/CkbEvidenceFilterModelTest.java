package com.hartwig.serve.sources.ckb.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.sources.ckb.blacklist.ImmutableCkbEvidenceFilterEntry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbEvidenceFilterModelTest {

    @Test
    public void canFilterAllEvidenceBasedOnGene() {
        CkbEvidenceFilterEntry filterEntry1 =
                create(CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE, "Nivolumab", null, "KRAS", null, null);
        CkbEvidenceFilterModel model = CkbFilteringTestFactory.createSpecificEvidenceFilterModel(filterEntry1);

        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", filterEntry1));
        assertFalse(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification", filterEntry1));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbEvidenceFilterEntry filterEntry2 =
                create(CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE, "Nivolumab", null, "KRAS", null, EvidenceLevel.C);
        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification", filterEntry2));
        assertFalse(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", filterEntry2));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification"));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "BRAF", "amplification"));
    }

    @Test
    public void canFilterAllEvidenceBasedOnGeneAndEvent() {
        CkbEvidenceFilterEntry filterEntry1 =
                create(CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT, "Nivolumab", null, "KRAS", "amplification", null);
        CkbEvidenceFilterModel model = CkbFilteringTestFactory.createSpecificEvidenceFilterModel(filterEntry1);

        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", filterEntry1));
        assertFalse(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification", filterEntry1));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbEvidenceFilterEntry filterEntry2 = create(CkbEvidenceFilterType.ALL_EVIDENCE_BASED_ON_GENE_AND_EVENT,
                "Nivolumab",
                null,
                "KRAS",
                "amplification",
                EvidenceLevel.C);
        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification", filterEntry2));
        assertFalse(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", filterEntry2));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.C, "KRAS", "amplification"));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }

    @Test
    public void canBlacklistEvidenceOnTherapy() {
        CkbEvidenceFilterEntry filterEntry1 = create(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, null);
        CkbEvidenceFilterModel model = CkbFilteringTestFactory.createSpecificEvidenceFilterModel(filterEntry1);

        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", filterEntry1));
        assertFalse(model.isMatch("Chemo", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification", filterEntry1));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Chemo", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));

        CkbEvidenceFilterEntry filterEntry2 =
                create(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY, "Nivolumab", null, null, null, EvidenceLevel.B);
        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification", filterEntry2));
        assertFalse(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", filterEntry2));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Chemo", "Solid tumor", EvidenceLevel.A, "BRAS", "amplification"));
    }

    @Test
    public void canFilterEvidenceOnTherapyAndCancerType() {
        CkbEvidenceFilterEntry filterEntry1 =
                create(CkbEvidenceFilterType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE, "Nivolumab", "Solid tumor", null, null, null);
        CkbEvidenceFilterModel model = CkbFilteringTestFactory.createSpecificEvidenceFilterModel(filterEntry1);

        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", filterEntry1));
        assertFalse(model.isMatch("Nivolumab", "Colorectum", EvidenceLevel.A, "BRAF", "amplification", filterEntry1));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Nivolumab", "Colorectum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbEvidenceFilterEntry filterEntry2 =
                create(CkbEvidenceFilterType.EVIDENCE_ON_THERAPY_AND_CANCER_TYPE, "Nivolumab", "Solid tumor", null, null, EvidenceLevel.A);
        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", filterEntry2));
        assertFalse(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification", filterEntry2));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Nivolumab", "Colon", EvidenceLevel.B, "KRAS", "amplification"));
    }

    @Test
    public void canFilterEvidenceOnTherapyAndCancerTypeAndGene() {
        CkbEvidenceFilterEntry filterEntry1 = create(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                null,
                null);
        CkbEvidenceFilterModel model = CkbFilteringTestFactory.createSpecificEvidenceFilterModel(filterEntry1);

        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", filterEntry1));
        assertFalse(model.isMatch("Nivolumab", "Colorectum", EvidenceLevel.A, "BRAF", "amplification", filterEntry1));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Nivolumab", "Colorectum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbEvidenceFilterEntry filterEntry2 = create(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                null,
                EvidenceLevel.B);
        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification", filterEntry2));
        assertFalse(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", filterEntry2));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.B, "KRAS", "amplification"));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }

    @Test
    public void canFilterEvidenceOnTherapyAndCancerTypeAndGeneAndEvent() {
        CkbEvidenceFilterEntry filterEntry1 = create(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                null);
        CkbEvidenceFilterModel model = CkbFilteringTestFactory.createSpecificEvidenceFilterModel(filterEntry1);

        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification", filterEntry1));
        assertFalse(model.isMatch("Nivolumab", "Colorectum", EvidenceLevel.A, "BRAF", "amplification", filterEntry1));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Nivolumab", "Colorectum", EvidenceLevel.A, "BRAF", "amplification"));

        CkbEvidenceFilterEntry filterEntry2 = create(CkbEvidenceFilterType.EVIDENCE_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification",
                EvidenceLevel.D);
        assertTrue(model.isMatch("Nivolumab", "Solid tumor", EvidenceLevel.D, "KRAS", "amplification", filterEntry2));
        assertFalse(model.isMatch("Nivolumab", "Colorectum", EvidenceLevel.A, "BRAF", "amplification", filterEntry2));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.D, "KRAS", "amplification"));
        assertTrue(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "KRAS", "amplification"));
        assertFalse(model.shouldFilterEvidence("Nivolumab", "Solid tumor", EvidenceLevel.A, "BRAF", "amplification"));
    }

    @NotNull
    private static CkbEvidenceFilterEntry create(@NotNull CkbEvidenceFilterType type, @Nullable String therapy, @Nullable String cancerType,
            @Nullable String gene, @Nullable String event, @Nullable EvidenceLevel level) {
        return ImmutableCkbEvidenceFilterEntry.builder()
                .type(type)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .level(level)
                .build();
    }
}