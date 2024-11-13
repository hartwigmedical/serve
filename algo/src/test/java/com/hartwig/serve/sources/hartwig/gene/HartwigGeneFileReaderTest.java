package com.hartwig.serve.sources.hartwig.gene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.common.GeneRole;

import org.junit.Test;

public class HartwigGeneFileReaderTest {

    private static final String DRIVER_GENES_EXAMPLE_TSV = Resources.getResource("hartwig/hartwig_driver_genes.tsv").getPath();
    private static final String CURATED_GENES_EXAMPLE_TSV = Resources.getResource("hartwig/hartwig_curated_genes.tsv").getPath();

    @Test
    public void canReadHartwigDriverGenesFile() throws IOException {
        List<HartwigGeneEntry> entries = HartwigGeneFileReader.readDriverGenes(DRIVER_GENES_EXAMPLE_TSV);

        assertEquals(1, entries.size());
        assertEquals("APC", entries.get(0).gene());
        assertEquals(GeneRole.TSG, entries.get(0).geneRole());
    }

    @Test
    public void canReadHartwigCuratedGenesFile() throws IOException {
        List<HartwigGeneEntry> entries = HartwigGeneFileReader.readCuratedGenes(CURATED_GENES_EXAMPLE_TSV);

        assertEquals(2, entries.size());
        assertEquals("TERT", entries.get(0).gene());
        assertEquals(GeneRole.ONCO, entries.get(0).geneRole());
        assertEquals("BRAF", entries.get(1).gene());
        assertEquals(GeneRole.ONCO, entries.get(1).geneRole());
    }
}