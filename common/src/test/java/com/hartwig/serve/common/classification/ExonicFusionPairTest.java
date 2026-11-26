package com.hartwig.serve.common.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExonicFusionPairTest {

    @Test
    public void canParseExonicFusionPairs() {
        ExonicFusionPair pair = ExonicFusionPair.fromEvent("RUFY2 exon9::RET exon12");

        assertNotNull(pair);
        assertEquals("RUFY2", pair.geneUp());
        assertEquals(9, pair.exonUp());
        assertEquals("RET", pair.geneDown());
        assertEquals(12, pair.exonDown());
    }

    @Test
    public void canParseExonicFusionPairsOnGenesWithDashes() {
        ExonicFusionPair pair = ExonicFusionPair.fromEvent("NKX2-1 exon2::HLA-A exon3");

        assertNotNull(pair);
        assertEquals("NKX2-1", pair.geneUp());
        assertEquals(2, pair.exonUp());
        assertEquals("HLA-A", pair.geneDown());
        assertEquals(3, pair.exonDown());
    }

    @Test
    public void canAssessWhetherEventIsExonicFusionPair() {
        assertTrue(ExonicFusionPair.isExonicFusionPair("RUFY2 exon9::RET exon12"));

        assertFalse(ExonicFusionPair.isExonicFusionPair("RUFY2 exon9::RET"));
        assertFalse(ExonicFusionPair.isExonicFusionPair("RUFY2::RET"));
        assertFalse(ExonicFusionPair.isExonicFusionPair("MET-FGFR2"));
        assertFalse(ExonicFusionPair.isExonicFusionPair("MET del exon 14"));
        assertFalse(ExonicFusionPair.isExonicFusionPair("exon 11"));
        assertFalse(ExonicFusionPair.isExonicFusionPair("HLA-A*02:01"));

        assertNull(ExonicFusionPair.fromEvent("RUFY2::RET"));
    }
}
