package com.hartwig.serve.sources.ckbtrial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.serve.cancertype.CancerTypeConstants;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableLocation;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableVariantRequirementDetail;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class ActionableTrialFactoryTest {

    @Test
    public void canCreateActionableTrials() {
        CkbEntry entryCharacteristics =
                CkbTrialTestFactory.createEntry("-", "MSI neg", "MSI neg", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        Set<ActionableTrial> entryCharacteristicsSet =
                ActionableTrialFactory.toActionableTrials(entryCharacteristics, Strings.EMPTY);
        assertEquals(1, entryCharacteristicsSet.size());
        ActionableTrial characteristics = entryCharacteristicsSet.iterator().next();
        assertEquals(Strings.EMPTY, characteristics.sourceEvent());
        assertEquals(Knowledgebase.CKB_TRIAL, characteristics.source());
        assertEquals("nctid", characteristics.treatment().name());
        assertEquals("AB", characteristics.applicableCancerType().name());
        assertEquals("162", characteristics.applicableCancerType().doid());
        assertEquals(EvidenceLevel.B, characteristics.level());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristics.direction());

        CkbEntry entryAmplification = CkbTrialTestFactory.createEntry("KRAS",
                "KRAS amplification",
                "KRAS amplification",
                "sensitive",
                "Actionable",
                "AB",
                "AB",
                "A",
                "DOID:163");
        Set<ActionableTrial> trialAmplificationSet =
                ActionableTrialFactory.toActionableTrials(entryAmplification, "KRAS");
        assertEquals(1, trialAmplificationSet.size());
        ActionableTrial amplification = trialAmplificationSet.iterator().next();
        assertEquals("KRAS", amplification.sourceEvent());
        assertEquals(Knowledgebase.CKB_TRIAL, amplification.source());
        assertEquals("nctid", amplification.treatment().name());
        assertEquals("AB", amplification.applicableCancerType().name());
        assertEquals("163", amplification.applicableCancerType().doid());
        assertTrue(amplification.blacklistCancerTypes().isEmpty());
        assertEquals(EvidenceLevel.B, amplification.level());
        assertEquals(EvidenceDirection.RESPONSIVE, amplification.direction());

        CkbEntry entryHotspot =
                CkbTrialTestFactory.createEntry("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        Set<ActionableTrial> entryHotspotSet =
                ActionableTrialFactory.toActionableTrials(entryHotspot, "BRAF");
        assertEquals(1, entryHotspotSet.size());
        ActionableTrial hotspot = entryHotspotSet.iterator().next();
        assertEquals("BRAF", hotspot.sourceEvent());
        assertEquals(Knowledgebase.CKB_TRIAL, hotspot.source());
        assertEquals("nctid", hotspot.treatment().name());
        assertEquals("AB", hotspot.applicableCancerType().name());
        assertEquals("162", hotspot.applicableCancerType().doid());
        assertEquals(EvidenceLevel.B, characteristics.level());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristics.direction());
    }

    @Test
    public void canExtractAndCurateDoid() {
        assertNull(com.hartwig.serve.sources.ckbtrial.ActionableTrialFactory.extractAndCurateDoid(null));
        assertNull(com.hartwig.serve.sources.ckbtrial.ActionableTrialFactory.extractAndCurateDoid(new String[] { "jax", "not a doid" }));

        assertEquals("0060463",
                com.hartwig.serve.sources.ckbtrial.ActionableTrialFactory.extractAndCurateDoid(new String[] { "DOID", "0060463" }));
        assertEquals(CancerTypeConstants.CANCER_DOID,
                com.hartwig.serve.sources.ckbtrial.ActionableTrialFactory.extractAndCurateDoid(new String[] { "JAX", "10000003" }));
        assertEquals(CancerTypeConstants.SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY,
                com.hartwig.serve.sources.ckbtrial.ActionableTrialFactory.extractAndCurateDoid(new String[] { "JAX", "10000009" }));
        assertEquals(CancerTypeConstants.ADENOCARCINOMA_OF_UNKNOWN_PRIMARY,
                com.hartwig.serve.sources.ckbtrial.ActionableTrialFactory.extractAndCurateDoid(new String[] { "JAX", "10000008" }));
        assertNull(com.hartwig.serve.sources.ckbtrial.ActionableTrialFactory.extractAndCurateDoid(new String[] { "JAX", "10000004" }));
    }

    @Test
    public void canExtractSourceCancerTypeID() {
        assertNull(ActionableTrialFactory.extractSourceCancerTypeId(null));
        assertNull(ActionableTrialFactory.extractSourceCancerTypeId("not a doid"));

        assertNotNull(ActionableTrialFactory.extractSourceCancerTypeId("DOID:0060463"));
        assertEquals("0060463", ActionableTrialFactory.extractSourceCancerTypeId("DOID:0060463")[1]);
        assertEquals("10000003", ActionableTrialFactory.extractSourceCancerTypeId("JAX:10000003")[1]);
    }

    @Test
    public void canDetermineIfHasUsableRequirementType() {
        ClinicalTrial trialWithRequiredType = CkbTrialTestFactory.trialWithRequirementType(Lists.newArrayList(
                ImmutableVariantRequirementDetail.builder().profileId(0).requirementType("required").build()));
        ClinicalTrial trialWithExcludedType = CkbTrialTestFactory.trialWithRequirementType(Lists.newArrayList(
                ImmutableVariantRequirementDetail.builder().profileId(0).requirementType("excluded").build()));
        CkbEntry entry = CkbTrialTestFactory.createEntry("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        assertTrue(ActionableTrialFactory.hasUsableRequirementType(trialWithRequiredType.variantRequirementDetails(), entry));
        assertFalse(ActionableTrialFactory.hasUsableRequirementType(trialWithExcludedType.variantRequirementDetails(), entry));
    }

    @Test
    public void canDetermineIfHasUsableCountry() {
        ClinicalTrial dutchTrial = CkbTrialTestFactory.trialWithCountry(Lists.newArrayList(ImmutableLocation.builder().nctId("").city("").country("Netherlands").build()));
        ClinicalTrial germanTrial = CkbTrialTestFactory.trialWithCountry(Lists.newArrayList(ImmutableLocation.builder().nctId("").city("").country("Germany").build()));
        assertTrue(ActionableTrialFactory.hasUsableCountry(dutchTrial.locations()));
        assertFalse(ActionableTrialFactory.hasUsableCountry(germanTrial.locations()));
    }

    @Test
    public void canDetermineIfHasUsableRecruitment() {
        assertTrue(ActionableTrialFactory.hasUsableRecruitment("Recruiting"));
        assertTrue(ActionableTrialFactory.hasUsableRecruitment("Active, not recruiting"));
        assertTrue(ActionableTrialFactory.hasUsableRecruitment("Unknown status"));
        assertFalse(ActionableTrialFactory.hasUsableRecruitment("Terminated"));
        assertFalse(ActionableTrialFactory.hasUsableRecruitment("Suspended"));
    }
}