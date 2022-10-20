package com.hartwig.serve.datamodel.genome.refgenome;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RefGenomeFunctionsTest {

    @Test
    public void canVersionChromosomes() {
        String chr37 = "10";
        String chr19 = "chr10";
        String chr38 = "chr10";

        assertEquals(chr37, RefGenomeFunctions.versionedChromosome(chr19, RefGenomeVersion.V37));
        assertEquals(chr37, RefGenomeFunctions.versionedChromosome(chr37, RefGenomeVersion.V37));
        assertEquals(chr37, RefGenomeFunctions.versionedChromosome(chr38, RefGenomeVersion.V37));

        assertEquals(chr38, RefGenomeFunctions.versionedChromosome(chr19, RefGenomeVersion.V38));
        assertEquals(chr38, RefGenomeFunctions.versionedChromosome(chr37, RefGenomeVersion.V38));
        assertEquals(chr38, RefGenomeFunctions.versionedChromosome(chr38, RefGenomeVersion.V38));
    }

    @Test
    public void canStripChromosomes() {
        assertEquals("10", RefGenomeFunctions.stripChrPrefix("chr10"));
        assertEquals("10", RefGenomeFunctions.stripChrPrefix("10"));
    }

    @Test
    public void canEnforceChromosomes() {
        assertEquals("chr10", RefGenomeFunctions.enforceChrPrefix("chr10"));
        assertEquals("chr10", RefGenomeFunctions.enforceChrPrefix("10"));
    }
}