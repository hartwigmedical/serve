package com.hartwig.serve.extraction.hotspot.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AnnotatedHotspotVCFCheckerPAVETest {

    @Test
    public void canSmartMatch() {
        // Hotspots should never smart-match
        assertFalse(AnnotatedHotspotVCFCheckerPAVE.isApproximateIndelMatch("p.K618I", "p.K617I"));

        // These DELs can be attributed to alignment
        assertTrue(AnnotatedHotspotVCFCheckerPAVE.isApproximateIndelMatch("p.K618del", "p.K617del"));
        assertTrue(AnnotatedHotspotVCFCheckerPAVE.isApproximateIndelMatch("p.D233del", "p.D225del"));
        assertTrue(AnnotatedHotspotVCFCheckerPAVE.isApproximateIndelMatch("p.T942_T943del", "p.T948_T949del"));

        // This is just a dup rephrased an insert.
        assertTrue(AnnotatedHotspotVCFCheckerPAVE.isApproximateIndelMatch("p.S1777_Y1787dup", "p.Y1787_K1788insSAATGHAASTT"));
    }
}