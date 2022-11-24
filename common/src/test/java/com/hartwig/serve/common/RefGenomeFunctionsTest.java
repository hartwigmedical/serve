package com.hartwig.serve.common;

import static org.junit.Assert.assertEquals;

import com.hartwig.serve.datamodel.RefGenome;

import org.junit.Test;

public class RefGenomeFunctionsTest {

    @Test
    public void canVersionChromosomes() {
        String chr37 = "10";
        String chr19 = "chr10";
        String chr38 = "chr10";

        assertEquals(chr37, RefGenomeFunctions.versionedChromosome(chr19, RefGenome.V37));
        assertEquals(chr37, RefGenomeFunctions.versionedChromosome(chr37, RefGenome.V37));
        assertEquals(chr37, RefGenomeFunctions.versionedChromosome(chr38, RefGenome.V37));

        assertEquals(chr38, RefGenomeFunctions.versionedChromosome(chr19, RefGenome.V38));
        assertEquals(chr38, RefGenomeFunctions.versionedChromosome(chr37, RefGenome.V38));
        assertEquals(chr38, RefGenomeFunctions.versionedChromosome(chr38, RefGenome.V38));
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