package com.hartwig.serve.sources.ckb.filter;

import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.sources.ckb.CkbTestFactory;
import com.hartwig.serve.sources.ckb.blacklist.ImmutableCkbMolecularProfileFilterEntry;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CkbMolecularProfileFilterModelTest {

    @Test
    public void canFilterOnKeywords() {
        CkbMolecularProfileFilterModel model =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.FILTER_EVENT_WITH_KEYWORD,
                        "benign"));
        CkbEntry entry = CkbTestFactory.createEntryWithVariant("filter benign me!");
        assertTrue(model.run(Lists.newArrayList(entry)).isEmpty());

        model.reportUnusedFilterEntries();
    }

    @Test
    public void canFilterOnGenes() {
        CkbMolecularProfileFilterModel modelForGene =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.FILTER_ALL_EVIDENCE_ON_GENE,
                        "gene"));
        CkbEntry entryGene = CkbTestFactory.createEntryWithGene("gene");
        assertTrue(modelForGene.run(Lists.newArrayList(entryGene)).isEmpty());
        modelForGene.reportUnusedFilterEntries();

        CkbMolecularProfileFilterModel modelForExonOnGene = new CkbMolecularProfileFilterModel(createFilterEntryList(
                CkbMolecularProfileFilterType.FILTER_EVIDENCE_FOR_EXONS_ON_GENE,
                "gene"));
        CkbEntry entryExon = CkbTestFactory.createEntryWithGeneAndVariant("gene", "exon 1");
        assertTrue(modelForExonOnGene.run(Lists.newArrayList(entryExon)).isEmpty());
        modelForExonOnGene.reportUnusedFilterEntries();
    }

    @Test
    public void canRemoveUnresolvableFusionLegs() {
        CkbMolecularProfileFilterModel model =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.FILTER_SECONDARY_GENE_WHEN_FUSION_LEG,
                        "FILT"));
        CkbEntry unresolvableEntry = CkbTestFactory.createEntryWithGeneAndVariant("BRAF", "FILT-BRAF");
        assertTrue(model.run(Lists.newArrayList(unresolvableEntry)).isEmpty());
    }

    @Test
    public void canRemoveExclusiveFusionGenes() {
        CkbMolecularProfileFilterModel model =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.ALLOW_GENE_IN_FUSIONS_EXCLUSIVELY,
                        "gene"));
        CkbEntry exclusiveFusionEntry = CkbTestFactory.createEntryWithGeneAndVariant("gene", "gene mutant");
        assertTrue(model.run(Lists.newArrayList(exclusiveFusionEntry)).isEmpty());
    }

    @Test
    public void canFilterOnFullNames() {
        CkbMolecularProfileFilterModel model =
                new CkbMolecularProfileFilterModel(createFilterEntryList(CkbMolecularProfileFilterType.FILTER_EXACT_VARIANT_FULLNAME,
                        "BRAF V600E"));
        CkbEntry entry = CkbTestFactory.createEntryWithFullName("BRAF V600E");
        assertTrue(model.run(Lists.newArrayList(entry)).isEmpty());
    }

    @NotNull
    private static List<CkbMolecularProfileFilterEntry> createFilterEntryList(@NotNull CkbMolecularProfileFilterType type,
            @NotNull String value) {
        return Lists.newArrayList(ImmutableCkbMolecularProfileFilterEntry.builder().type(type).value(value).build());
    }
}