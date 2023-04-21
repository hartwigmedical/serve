package com.hartwig.serve.sources.hartwig.gene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class HartwigGeneFileReaderTest {

    private static final String HARTWIG_GENES_CURATED = Resources.getResource("hartwig/hartwig_genes_curated.tsv").getPath();

    @Test
    public void canReadHartwigCuratedGenesFile() throws IOException {
        List<HartwigGeneEntry> entries = HartwigGeneFileReader.read(HARTWIG_GENES_CURATED);

        assertEquals(2, entries.size());

        assertEquals("TERT", entries.get(0).gene());
        assertEquals("BRAF", entries.get(1).gene());
    }
}