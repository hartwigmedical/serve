package com.hartwig.serve.sources.hartwig.gene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class HartwigGeneFileReaderTest {

    private static final String EXAMPLE_TSV = Resources.getResource("hartwig/hartwig_curated_genes.tsv").getPath();

    @Test
    public void canReadHartwigCuratedGenesFile() throws IOException {
        List<HartwigGeneEntry> entries = HartwigGeneFileReader.read(EXAMPLE_TSV, "geneRole");

        assertEquals(2, entries.size());

        assertEquals("TERT", entries.get(0).gene());
        assertEquals("BRAF", entries.get(1).gene());
    }
}