package com.hartwig.serve.sources.docm.curation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.sources.docm.DocmEntry;
import com.hartwig.serve.sources.docm.ImmutableDocmEntry;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class DocmCuratorTest {

    @Test
    public void canFilterDocmEntries() {
        DocmEntry validEntry = ImmutableDocmEntry.builder().gene("gene").transcript("transcript").proteinAnnotation("annotation").build();

        CurationKey firstFilterKey = firstFilterKey();
        DocmEntry filterEntry = ImmutableDocmEntry.builder()
                .gene(firstFilterKey.gene())
                .transcript(firstFilterKey.transcript())
                .proteinAnnotation(firstFilterKey.proteinAnnotation())
                .build();

        List<DocmEntry> entries = Lists.newArrayList(validEntry, filterEntry);

        DocmCurator curator = new DocmCurator();
        List<DocmEntry> curated = curator.curate(entries);

        assertEquals(1, curated.size());
        assertEquals(validEntry, curated.get(0));

        curator.reportUnusedFilterEntries();
    }

    @Test
    public void canMapGenes() {
        String firstMappableGene = firstGeneForMapping();
        DocmEntry entry =
                ImmutableDocmEntry.builder().gene(firstMappableGene).transcript(Strings.EMPTY).proteinAnnotation(Strings.EMPTY).build();

        DocmCurator curator = new DocmCurator();
        List<DocmEntry> curated = curator.curate(Lists.newArrayList(entry));

        assertEquals(1, curated.size());
        assertEquals(CurationFactory.GENE_MAPPINGS.get(firstMappableGene), curated.get(0).gene());
    }

    @NotNull
    private static String firstGeneForMapping() {
        return CurationFactory.GENE_MAPPINGS.keySet().iterator().next();
    }

    @NotNull
    private static CurationKey firstFilterKey() {
        return CurationFactory.ENTRY_FILTERS.iterator().next();
    }
}