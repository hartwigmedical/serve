package com.hartwig.serve.sources.ckb.filter;

import static com.hartwig.serve.sources.ckb.CkbTestFactory.createCombinedEntry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.variant.Variant;
import com.hartwig.serve.sources.ckb.CkbTestFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CkbMolecularProfileFilterModelTest {

    @Test
    public void canFilterOnKeywords() {
        CkbMolecularProfileFilterModel model =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.FILTER_EVENT_WITH_KEYWORD,
                        "benign"));
        CkbEntry entry = CkbTestFactory.createEntryWithVariant("filter benign me!");
        assertTrue(model.run(List.of(entry)).isEmpty());

        model.reportUnusedFilterEntries();
    }

    @Test
    public void canFilterOnGenes() {
        CkbMolecularProfileFilterModel modelForGene =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.FILTER_ALL_EVIDENCE_ON_GENE,
                        "gene"));
        CkbEntry entryGene = CkbTestFactory.createEntryWithGene("gene");
        assertTrue(modelForGene.run(List.of(entryGene)).isEmpty());
        modelForGene.reportUnusedFilterEntries();

        CkbMolecularProfileFilterModel modelForExonOnGene = new CkbMolecularProfileFilterModel(createFilterEntryList(
                CkbMolecularProfileFilterType.FILTER_EVIDENCE_FOR_EXONS_ON_GENE,
                "gene"));
        CkbEntry entryExon = CkbTestFactory.createEntryWithGeneAndVariant("gene", "exon 1");
        assertTrue(modelForExonOnGene.run(List.of(entryExon)).isEmpty());
        modelForExonOnGene.reportUnusedFilterEntries();
    }

    @Test
    public void canRemoveUnresolvableFusionLegs() {
        CkbMolecularProfileFilterModel model =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.FILTER_SECONDARY_GENE_WHEN_FUSION_LEG,
                        "FILT"));
        CkbEntry unresolvableEntry = CkbTestFactory.createEntryWithGeneAndVariant("BRAF", "FILT-BRAF");
        assertTrue(model.run(List.of(unresolvableEntry)).isEmpty());
    }

    @Test
    public void canRemoveExclusiveFusionGenes() {
        CkbMolecularProfileFilterModel model =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.ALLOW_GENE_IN_FUSIONS_EXCLUSIVELY,
                        "gene"));
        CkbEntry exclusiveFusionEntry = CkbTestFactory.createEntryWithGeneAndVariant("gene", "gene mutant");
        assertTrue(model.run(List.of(exclusiveFusionEntry)).isEmpty());
    }

    @Test
    public void canFilterOnFullNames() {
        CkbMolecularProfileFilterModel model =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.FILTER_EXACT_VARIANT_FULLNAME,
                        "BRAF V600E"));
        CkbEntry entry = CkbTestFactory.createEntryWithFullName("BRAF V600E");
        assertTrue(model.run(List.of(entry)).isEmpty());
    }

    @Test
    public void canAcceptCombinedProfilesContainingAllValidVariants() {
        CkbMolecularProfileFilterModel model =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.FILTER_EVENT_WITH_KEYWORD,
                        "hypermethylation"));

        List<Variant> variants = List.of(
                CkbTestFactory.createVariant("BRAF", "V600E", "BRAF V600E"),
                CkbTestFactory.createVariant("MLH1", "del", "MLH1 del")
        );

        List<CkbEntry> ckbEntries = List.of(createCombinedEntry(variants));
        assertEquals(model.run(ckbEntries), ckbEntries);
    }

    @Test
    public void canRemoveCombinedProfilesContainingSomeFilteredVariants() {
        CkbMolecularProfileFilterModel model =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.FILTER_EVENT_WITH_KEYWORD,
                        "hypermethylation"));

        List<Variant> variants = List.of(
                CkbTestFactory.createVariant("BRAF", "V600E", "BRAF V600E"),
                CkbTestFactory.createVariant("MLH1", "hypermethylation", "MLH1 hypermethylation")
        );

        List<CkbEntry> ckbEntries = List.of(createCombinedEntry(variants));
        assertTrue(model.run(ckbEntries).isEmpty());
    }

    @Test
    public void canRemoveEmptyProfiles() {
        CkbMolecularProfileFilterModel model =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.FILTER_EVENT_WITH_KEYWORD,
                        "hypermethylation"));

        List<CkbEntry> ckbEntries = List.of(createCombinedEntry(List.of()));
        assertTrue(model.run(ckbEntries).isEmpty());
    }

    @NotNull
    private static List<CkbMolecularProfileFilterEntry> createFilterEntryList(@NotNull CkbMolecularProfileFilterType type,
            @NotNull String value) {
        return Lists.newArrayList(ImmutableCkbMolecularProfileFilterEntry.builder().type(type).value(value).build());
    }
}