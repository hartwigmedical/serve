package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EvidenceDirectionTest {

    @Test
    public void canCompareEvidenceDirections() {
        assertTrue(EvidenceDirection.RESISTANT.isHigher(EvidenceDirection.PREDICTED_RESISTANT));
        assertTrue(EvidenceDirection.RESPONSIVE.isHigher(EvidenceDirection.PREDICTED_RESPONSIVE));
        assertTrue(EvidenceDirection.RESPONSIVE.isHigher(EvidenceDirection.RESISTANT));
        assertFalse(EvidenceDirection.RESISTANT.isHigher(EvidenceDirection.RESPONSIVE));
        assertFalse(EvidenceDirection.PREDICTED_RESISTANT.isHigher(EvidenceDirection.RESISTANT));
        assertFalse(EvidenceDirection.PREDICTED_RESPONSIVE.isHigher(EvidenceDirection.RESPONSIVE));
        assertFalse(EvidenceDirection.NO_BENEFIT.isHigher(EvidenceDirection.RESPONSIVE));
        assertFalse(EvidenceDirection.NO_BENEFIT.isHigher(EvidenceDirection.RESISTANT));
    }

}