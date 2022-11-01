package com.hartwig.serve.extraction.hotspot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MitochondrialChromosomeTest {

    @Test
    public void canDetermineIfContained() {
        assertTrue(MitochondrialChromosome.contains("MT"));
        assertTrue(MitochondrialChromosome.contains("chrM"));

        assertFalse(MitochondrialChromosome.contains("1"));
        assertFalse(MitochondrialChromosome.contains("chr1"));
        assertFalse(MitochondrialChromosome.contains("X"));
        assertFalse(MitochondrialChromosome.contains("chrX"));
    }
}
