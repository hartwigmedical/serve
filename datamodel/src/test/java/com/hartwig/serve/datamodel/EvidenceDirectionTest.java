package com.hartwig.serve.datamodel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EvidenceDirectionTest {

    @Test
    public void canDefineBooleansForResponsiveEvidence() {
        assertTrue(EvidenceDirection.RESPONSIVE.hasPositiveResponse());
        assertTrue(EvidenceDirection.RESPONSIVE.hasBenefit());
        assertFalse(EvidenceDirection.RESPONSIVE.isResistant());
        assertTrue(EvidenceDirection.RESPONSIVE.isCertain());
    }

    @Test
    public void canDefineBooleansForPredictiveResponsiveEvidence() {
        assertTrue(EvidenceDirection.PREDICTED_RESPONSIVE.hasPositiveResponse());
        assertTrue(EvidenceDirection.PREDICTED_RESPONSIVE.hasBenefit());
        assertFalse(EvidenceDirection.PREDICTED_RESPONSIVE.isResistant());
        assertFalse(EvidenceDirection.PREDICTED_RESPONSIVE.isCertain());
    }

    @Test
    public void canDefineBooleansForDecreasedResponseEvidence() {
        assertFalse(EvidenceDirection.DECREASED_RESPONSE.hasPositiveResponse());
        assertTrue(EvidenceDirection.DECREASED_RESPONSE.hasBenefit());
        assertFalse(EvidenceDirection.DECREASED_RESPONSE.isResistant());
        assertTrue(EvidenceDirection.DECREASED_RESPONSE.isCertain());
    }

    @Test
    public void canDefineBooleansForNoBenefitEvidence() {
        assertFalse(EvidenceDirection.NO_BENEFIT.hasPositiveResponse());
        assertFalse(EvidenceDirection.NO_BENEFIT.hasBenefit());
        assertFalse(EvidenceDirection.NO_BENEFIT.isResistant());
        assertTrue(EvidenceDirection.NO_BENEFIT.isCertain());
    }

    @Test
    public void canDefineBooleansForResistantEvidence() {
        assertFalse(EvidenceDirection.RESISTANT.hasPositiveResponse());
        assertFalse(EvidenceDirection.RESISTANT.hasBenefit());
        assertTrue(EvidenceDirection.RESISTANT.isResistant());
        assertTrue(EvidenceDirection.RESISTANT.isCertain());
    }

    @Test
    public void canDefineBooleansForPredictiveResistantEvidence() {
        assertFalse(EvidenceDirection.PREDICTED_RESISTANT.hasPositiveResponse());
        assertFalse(EvidenceDirection.PREDICTED_RESISTANT.hasBenefit());
        assertTrue(EvidenceDirection.PREDICTED_RESISTANT.isResistant());
        assertFalse(EvidenceDirection.PREDICTED_RESISTANT.isCertain());
    }
}