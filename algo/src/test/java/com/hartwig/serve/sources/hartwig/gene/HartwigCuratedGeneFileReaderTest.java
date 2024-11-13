package com.hartwig.serve.sources.hartwig.gene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.common.GeneRole;

import org.junit.Test;

public class HartwigCuratedGeneFileReaderTest {

    private static final String EXAMPLE_TSV = Resources.getResource("hartwig/hartwig_curated_genes.tsv").getPath();

    @Test
    public void canReadHartwigCuratedGenesFile() throws IOException {
        List<HartwigGeneEntry> entries = HartwigCuratedGeneFileReader.read(EXAMPLE_TSV);

        assertEquals(2, entries.size());
        assertEquals("TERT", entries.get(0).gene());
        assertEquals(GeneRole.ONCO, entries.get(0).geneRole());
        assertEquals("BRAF", entries.get(1).gene());
        assertEquals(GeneRole.ONCO, entries.get(1).geneRole());
    }
}