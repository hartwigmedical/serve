package com.hartwig.serve.datamodel;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EvidenceDirectionTest  {

    @Test
    public void canCompareEvidenceDirections() {
        assertTrue(EvidenceDirection.RESISTANT.isHigher(EvidenceDirection.PREDICTED_RESISTANT));
        assertTrue(EvidenceDirection.RESPONSIVE.isHigher(EvidenceDirection.PREDICTED_RESPONSIVE));
        assertTrue(EvidenceDirection.RESPONSIVE.isHigher(EvidenceDirection.RESISTANT));
    }

}