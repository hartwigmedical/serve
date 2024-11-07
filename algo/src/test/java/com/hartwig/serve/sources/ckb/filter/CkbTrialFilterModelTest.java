package com.hartwig.serve.sources.ckb.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class CkbTrialFilterModelTest {

    @Test
    public void canFilterAllTrialsBasedOnGene() {
        CkbTrialFilterEntry filterEntry = create(CkbTrialFilterType.ALL_TRIALS_BASED_ON_GENE, "NCT1", null, null, "KRAS", null);
        CkbTrialFilterModel model = CkbFilteringTestFactory.createSpecificTrialFilterModel(filterEntry);

        assertTrue(model.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", filterEntry));
        assertFalse(model.isMatch("NCT1", "Nivolumab", "Solid tumor", "BRAF", "amplification", filterEntry));
        assertTrue(model.shouldFilterTrial("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(model.shouldFilterTrial("NCT1", "Nivolumab", "Solid tumor", "BRAF", "amplification"));
    }

    @Test
    public void canFilterAllTrialsBasedOnGeneAndEvent() {
        CkbTrialFilterEntry filterEntry =
                create(CkbTrialFilterType.ALL_TRIALS_BASED_ON_GENE_AND_EVENT, "NCT1", null, null, "KRAS", "amplification");
        CkbTrialFilterModel model = CkbFilteringTestFactory.createSpecificTrialFilterModel(filterEntry);

        assertTrue(model.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", filterEntry));
        assertFalse(model.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "deletion", filterEntry));
        assertTrue(model.shouldFilterTrial("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(model.shouldFilterTrial("NCT1", "Nivolumab", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canFilterCompleteTrial() {
        CkbTrialFilterEntry filterEntry = create(CkbTrialFilterType.COMPLETE_TRIAL, "NCT1", null, null, null, null);
        CkbTrialFilterModel model = CkbFilteringTestFactory.createSpecificTrialFilterModel(filterEntry);

        assertTrue(model.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", filterEntry));
        assertFalse(model.isMatch("NCT2", "Nivolumab", "Solid tumor", "KRAS", "deletion", filterEntry));
        assertTrue(model.shouldFilterTrial("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(model.shouldFilterTrial("NCT2", "Nivolumab", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canFilterTrialBasedOnTherapy() {
        CkbTrialFilterEntry filterEntry = create(CkbTrialFilterType.TRIAL_BASED_ON_THERAPY, "NCT1", "Nivolumab", null, null, null);
        CkbTrialFilterModel model = CkbFilteringTestFactory.createSpecificTrialFilterModel(filterEntry);

        assertTrue(model.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", filterEntry));
        assertFalse(model.isMatch("NCT1", "Chemo", "Solid tumor", "KRAS", "deletion", filterEntry));
        assertTrue(model.shouldFilterTrial("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(model.shouldFilterTrial("NCT1", "Chemo", "Solid tumor", "KRAS", "deletion"));
    }

    @Test
    public void canFilterTrialBasedOnTherapyAndCancerType() {
        CkbTrialFilterEntry filterEntry =
                create(CkbTrialFilterType.TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE, "NCT1", "Nivolumab", "Solid tumor", null, null);
        CkbTrialFilterModel model = CkbFilteringTestFactory.createSpecificTrialFilterModel(filterEntry);

        assertTrue(model.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", filterEntry));
        assertFalse(model.isMatch("NCT1", "Chemo", "Breast", "KRAS", "deletion", filterEntry));
        assertTrue(model.shouldFilterTrial("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(model.shouldFilterTrial("NCT1", "Chemo", "Breast", "KRAS", "deletion"));
    }

    @Test
    public void canFilterTrialBasedOnTherapyAndCancerTypeAndGene() {
        CkbTrialFilterEntry filterEntry = create(CkbTrialFilterType.TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE,
                "NCT1",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                null);
        CkbTrialFilterModel model = CkbFilteringTestFactory.createSpecificTrialFilterModel(filterEntry);

        assertTrue(model.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", filterEntry));
        assertFalse(model.isMatch("NCT1", "Chemo", "Breast", "PTEN", "deletion", filterEntry));
        assertTrue(model.shouldFilterTrial("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(model.shouldFilterTrial("NCT1", "Chemo", "Breast", "PTEN", "deletion"));
    }

    @Test
    public void canFilterTrialBasedOnTherapyAndCancerTypeAndGeneAndEvent() {
        CkbTrialFilterEntry filterEntry = create(CkbTrialFilterType.TRIAL_BASED_ON_THERAPY_AND_CANCER_TYPE_AND_GENE_AND_EVENT,
                "NCT1",
                "Nivolumab",
                "Solid tumor",
                "KRAS",
                "amplification");
        CkbTrialFilterModel model = CkbFilteringTestFactory.createSpecificTrialFilterModel(filterEntry);

        assertTrue(model.isMatch("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification", filterEntry));
        assertFalse(model.isMatch("NCT1", "Chemo", "Breast", "PTEN", "deletion", filterEntry));
        assertTrue(model.shouldFilterTrial("NCT1", "Nivolumab", "Solid tumor", "KRAS", "amplification"));
        assertFalse(model.shouldFilterTrial("NCT1", "Chemo", "Breast", "PTEN", "deletion"));
    }

    @NotNull
    private static CkbTrialFilterEntry create(@NotNull CkbTrialFilterType type, @NotNull String nctId, @Nullable String therapy,
            @Nullable String cancerType, @Nullable String gene, @Nullable String event) {
        return ImmutableCkbTrialFilterEntry.builder()
                .type(type)
                .nctId(nctId)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .build();
    }
}