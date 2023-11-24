package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachCurator;
import com.hartwig.serve.sources.ckb.treatmentapproach.TreatmentApproachTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class ActionableEvidenceFactoryTest {

    @Test
    public void canCreateActionableEntries() {
        TreatmentApproachCurator curator = TreatmentApproachTestFactory.createTestCurator();

        CkbEntry entryDeletion =
                CkbTestFactory.createEntry("KRAS", "deletion", "KRAS deletion", "sensitive", "Emerging", "AB", "AB", "A", "DOID:162");
        Set<ActionableEvidence> entryDeletionSet =
                ActionableEvidenceFactory.toActionableEvidence(entryDeletion, "KRAS", curator, "gene", entryDeletion.type());
        assertEquals(0, entryDeletionSet.size());

        CkbEntry entryCharacteristics =
                CkbTestFactory.createEntry("-", "MSI neg", "MSI neg", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        Set<ActionableEvidence> entryCharacteristicsSet =
                ActionableEvidenceFactory.toActionableEvidence(entryCharacteristics, Strings.EMPTY, curator, "-", entryCharacteristics.type());
        assertEquals(1, entryCharacteristicsSet.size());
        ActionableEvidence characteristics = entryCharacteristicsSet.iterator().next();
        assertEquals(Strings.EMPTY, characteristics.sourceEvent());
        assertEquals(Knowledgebase.CKB_EVIDENCE, characteristics.source());
        assertEquals("AB", characteristics.treatment().name());
        assertEquals("AB", characteristics.applicableCancerType().name());
        assertEquals("162", characteristics.applicableCancerType().doid());
        assertEquals(Sets.newHashSet(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE,
                CancerTypeConstants.BONE_MARROW_TYPE,
                CancerTypeConstants.LEUKEMIA_TYPE), characteristics.blacklistCancerTypes());
        assertEquals(EvidenceLevel.A, characteristics.level());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristics.direction());

        CkbEntry entryAmplification = CkbTestFactory.createEntry("KRAS",
                "KRAS amplification",
                "KRAS amplification",
                "sensitive",
                "Actionable",
                "AB",
                "AB",
                "A",
                "DOID:163");
        Set<ActionableEvidence> entryAmplificationSet =
                ActionableEvidenceFactory.toActionableEvidence(entryAmplification, "KRAS", curator, "KRAS", entryAmplification.type());
        assertEquals(1, entryAmplificationSet.size());
        ActionableEvidence amplification = entryAmplificationSet.iterator().next();
        assertEquals("KRAS", amplification.sourceEvent());
        assertEquals(Knowledgebase.CKB_EVIDENCE, amplification.source());
        assertEquals("AB", amplification.treatment().name());
        assertEquals("AB", amplification.applicableCancerType().name());
        assertEquals("163", amplification.applicableCancerType().doid());
        assertTrue(amplification.blacklistCancerTypes().isEmpty());
        assertEquals(EvidenceLevel.A, amplification.level());
        assertEquals(EvidenceDirection.RESPONSIVE, amplification.direction());

        CkbEntry entryHotspot =
                CkbTestFactory.createEntry("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        Set<ActionableEvidence> entryHotspotSet =
                ActionableEvidenceFactory.toActionableEvidence(entryHotspot, "BRAF", curator, "BRAF", entryHotspot.type());
        assertEquals(1, entryHotspotSet.size());
        ActionableEvidence hotspot = entryHotspotSet.iterator().next();
        assertEquals("BRAF", hotspot.sourceEvent());
        assertEquals(Knowledgebase.CKB_EVIDENCE, hotspot.source());
        assertEquals("AB", hotspot.treatment().name());
        assertEquals("AB", hotspot.applicableCancerType().name());
        assertEquals("162", hotspot.applicableCancerType().doid());
        assertEquals(Sets.newHashSet(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE,
                CancerTypeConstants.BONE_MARROW_TYPE,
                CancerTypeConstants.LEUKEMIA_TYPE), hotspot.blacklistCancerTypes());
        assertEquals(EvidenceLevel.A, characteristics.level());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristics.direction());
    }

    @Test
    public void canExtractAndCurateDoid() {
        assertNull(ActionableEvidenceFactory.extractAndCurateDoid(null));
        assertNull(ActionableEvidenceFactory.extractAndCurateDoid(new String[] { "jax", "not a doid" }));

        assertEquals("0060463", ActionableEvidenceFactory.extractAndCurateDoid(new String[] { "DOID", "0060463" }));
        assertEquals(CancerTypeConstants.CANCER_DOID, ActionableEvidenceFactory.extractAndCurateDoid(new String[] { "JAX", "10000003" }));
        assertEquals(CancerTypeConstants.SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY,
                ActionableEvidenceFactory.extractAndCurateDoid(new String[] { "JAX", "10000009" }));
        assertEquals(CancerTypeConstants.ADENOCARCINOMA_OF_UNKNOWN_PRIMARY,
                ActionableEvidenceFactory.extractAndCurateDoid(new String[] { "JAX", "10000008" }));
        assertNull(ActionableEvidenceFactory.extractAndCurateDoid(new String[] { "JAX", "10000004" }));
    }

    @Test
    public void canExtractSourceCancerTypeID() {
        assertNull(ActionableEvidenceFactory.extractSourceCancerTypeId(null));
        assertNull(ActionableEvidenceFactory.extractSourceCancerTypeId("not a doid"));

        assertNotNull(ActionableEvidenceFactory.extractSourceCancerTypeId("DOID:0060463"));
        assertEquals("0060463", ActionableEvidenceFactory.extractSourceCancerTypeId("DOID:0060463")[1]);
        assertEquals("10000003", ActionableEvidenceFactory.extractSourceCancerTypeId("JAX:10000003")[1]);
    }

    @Test
    public void canConvertToUrlString() {
        assertEquals("predicted+-+sensitive", ActionableEvidenceFactory.toUrlString("predicted - sensitive"));
        assertEquals("predicted+-+resistant", ActionableEvidenceFactory.toUrlString("predicted - resistant"));
        assertEquals("resistant", ActionableEvidenceFactory.toUrlString("resistant"));
        assertEquals("sensitive", ActionableEvidenceFactory.toUrlString("sensitive"));
    }

    @Test
    public void canDetermineIfHasUsableEvidenceType() {
        assertTrue(ActionableEvidenceFactory.hasUsableEvidenceType("Actionable"));
        assertFalse(ActionableEvidenceFactory.hasUsableEvidenceType("Prognostic"));
        assertFalse(ActionableEvidenceFactory.hasUsableEvidenceType("Emerging"));
        assertFalse(ActionableEvidenceFactory.hasUsableEvidenceType("Risk Factor"));
        assertFalse(ActionableEvidenceFactory.hasUsableEvidenceType("Diagnostic"));
    }

    @Test
    public void canResolveLevels() {
        assertNull(ActionableEvidenceFactory.resolveLevel("NA"));
        assertEquals(EvidenceLevel.A, ActionableEvidenceFactory.resolveLevel("A"));
        assertEquals(EvidenceLevel.B, ActionableEvidenceFactory.resolveLevel("B"));
        assertEquals(EvidenceLevel.C, ActionableEvidenceFactory.resolveLevel("C"));
        assertEquals(EvidenceLevel.D, ActionableEvidenceFactory.resolveLevel("D"));
    }

    @Test
    public void canResolveDirections() {
        assertNull(ActionableEvidenceFactory.resolveDirection(null));
        assertNull(ActionableEvidenceFactory.resolveDirection("unknown"));
        assertNull(ActionableEvidenceFactory.resolveDirection("not applicable"));
        assertNull(ActionableEvidenceFactory.resolveDirection("conflicting"));
        assertNull(ActionableEvidenceFactory.resolveDirection("no benefit"));
        assertNull(ActionableEvidenceFactory.resolveDirection("not predictive"));
        assertNull(ActionableEvidenceFactory.resolveDirection("decreased response"));

        assertEquals(EvidenceDirection.RESPONSIVE, ActionableEvidenceFactory.resolveDirection("sensitive"));
        assertEquals(EvidenceDirection.PREDICTED_RESPONSIVE, ActionableEvidenceFactory.resolveDirection("predicted - sensitive"));
        assertEquals(EvidenceDirection.RESISTANT, ActionableEvidenceFactory.resolveDirection("resistant"));
        assertEquals(EvidenceDirection.PREDICTED_RESISTANT, ActionableEvidenceFactory.resolveDirection("predicted - resistant"));
    }
}