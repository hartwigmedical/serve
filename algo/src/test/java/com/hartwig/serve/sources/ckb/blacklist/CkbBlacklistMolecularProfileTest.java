package com.hartwig.serve.sources.ckb.blacklist;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.sources.ckb.CkbTestFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class CkbBlacklistMolecularProfileTest{

    @Test
    public void canFilterOnKeywords() {
        CkbBlacklistMolecularProfile filter = new CkbBlacklistMolecularProfile(createFilterEntryList(CkbBlacklistMolecularProfileType.FILTER_EVENT_WITH_KEYWORD, "benign"));
        CkbEntry entry = CkbTestFactory.createEntryWithVariant("filter benign me!");
        assertTrue(filter.run(Lists.newArrayList(entry)).isEmpty());

        filter.reportUnusedBlacklistEntries();
    }

    @Test
    public void canFilterOnGenes() {
        CkbBlacklistMolecularProfile fullFilter = new CkbBlacklistMolecularProfile(createFilterEntryList(CkbBlacklistMolecularProfileType.FILTER_ALL_EVIDENCE_ON_GENE, "gene"));
        CkbEntry filterEntry = CkbTestFactory.createEntryWithGene("gene");
        assertTrue(fullFilter.run(Lists.newArrayList(filterEntry)).isEmpty());
        fullFilter.reportUnusedBlacklistEntries();

        CkbBlacklistMolecularProfile exonFilter = new CkbBlacklistMolecularProfile(createFilterEntryList(CkbBlacklistMolecularProfileType.FILTER_EVIDENCE_FOR_EXONS_ON_GENE, "gene"));
        CkbEntry filterExonEntry = CkbTestFactory.createEntryWithGeneAndVariant("gene", "exon 1");
        assertTrue(exonFilter.run(Lists.newArrayList(filterExonEntry)).isEmpty());
        exonFilter.reportUnusedBlacklistEntries();
    }

    @Test
    public void canRemoveUnresolvableFusionLegs() {
        CkbBlacklistMolecularProfile filter = new CkbBlacklistMolecularProfile(createFilterEntryList(CkbBlacklistMolecularProfileType.FILTER_SECONDARY_GENE_WHEN_FUSION_LEG, "FILT"));
        CkbEntry unresolvableEntry = CkbTestFactory.createEntryWithGeneAndVariant("BRAF", "FILT-BRAF");
        assertTrue(filter.run(Lists.newArrayList(unresolvableEntry)).isEmpty());
    }

    @Test
    public void canRemoveExclusiveFusionGenes() {
        CkbBlacklistMolecularProfile filter = new CkbBlacklistMolecularProfile(createFilterEntryList(CkbBlacklistMolecularProfileType.ALLOW_GENE_IN_FUSIONS_EXCLUSIVELY, "gene"));
        CkbEntry exclusiveFusionEntry = CkbTestFactory.createEntryWithGeneAndVariant("gene", "gene mutant");
        assertTrue(filter.run(Lists.newArrayList(exclusiveFusionEntry)).isEmpty());
    }

    @Test
    public void canFilterOnFullNames() {
        CkbBlacklistMolecularProfile filter = new CkbBlacklistMolecularProfile(createFilterEntryList(CkbBlacklistMolecularProfileType.FILTER_EXACT_VARIANT_FULLNAME, "BRAF V600E"));
        CkbEntry entry = CkbTestFactory.createEntryWithFullName("BRAF V600E");
        assertTrue(filter.run(Lists.newArrayList(entry)).isEmpty());
    }

    @NotNull
    private static List<CkbBlacklistMolecularProfileEntry> createFilterEntryList(@NotNull CkbBlacklistMolecularProfileType type, @NotNull String value) {
        return Lists.newArrayList(ImmutableCkbBlacklistMolecularProfileEntry.builder().type(type).value(value).build());
    }

}