package com.hartwig.serve.sources.hartwig.gene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.common.GeneRole;

import org.junit.Test;

public class HartwigDriverGeneFileReaderTest {

    private static final String EXAMPLE_TSV = Resources.getResource("hartwig/hartwig_driver_genes.tsv").getPath();

    @Test
    public void canReadHartwigDriverGenesFile() throws IOException {
        List<HartwigGeneEntry> entries = HartwigDriverGeneFileReader.read(EXAMPLE_TSV);

        assertEquals(1, entries.size());
        assertEquals("APC", entries.get(0).gene());
        assertEquals(GeneRole.TSG, entries.get(0).geneRole());
    }
}