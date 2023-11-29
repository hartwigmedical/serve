package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
        Set<ActionableEntry> entryCharacteristicsSet = ActionableTrialFactory.toActionableTrials(entryCharacteristics, Strings.EMPTY);
        assertEquals(1, entryCharacteristicsSet.size());
        ActionableEntry characteristics = entryCharacteristicsSet.iterator().next();
        assertEquals(Strings.EMPTY, characteristics.sourceEvent());
        assertEquals(Knowledgebase.CKB_TRIAL, characteristics.source());
        assertEquals("nctid", characteristics.treatment().name());
        assertEquals("AB", characteristics.applicableCancerType().name());
        assertEquals("162", characteristics.applicableCancerType().doid());
        assertTrue(characteristics.blacklistCancerTypes().isEmpty());
        assertEquals(EvidenceLevel.B, characteristics.level());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristics.direction());
        assertEquals("Netherlands", characteristics.evidenceUrls().iterator().next());

        CkbEntry entryAmplification = CkbTrialTestFactory.createEntry("KRAS",
                "KRAS amplification",
                "KRAS amplification",
                "sensitive",
                "Actionable",
                "AB",
                "AB",
                "A",
                "DOID:163");
        Set<ActionableEntry> trialAmplificationSet = ActionableTrialFactory.toActionableTrials(entryAmplification, "KRAS");
        assertEquals(1, trialAmplificationSet.size());
        ActionableEntry amplification = trialAmplificationSet.iterator().next();
        assertEquals("KRAS", amplification.sourceEvent());
        assertEquals(Knowledgebase.CKB_TRIAL, amplification.source());
        assertEquals("nctid", amplification.treatment().name());
        assertEquals("AB", amplification.applicableCancerType().name());
        assertEquals("163", amplification.applicableCancerType().doid());
        assertTrue(amplification.blacklistCancerTypes().isEmpty());
        assertEquals(EvidenceLevel.B, amplification.level());
        assertEquals(EvidenceDirection.RESPONSIVE, amplification.direction());
        assertEquals("Netherlands", characteristics.evidenceUrls().iterator().next());

        CkbEntry entryHotspot =
                CkbTrialTestFactory.createEntry("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        Set<ActionableEntry> entryHotspotSet = ActionableTrialFactory.toActionableTrials(entryHotspot, "BRAF");
        assertEquals(1, entryHotspotSet.size());
        ActionableEntry hotspot = entryHotspotSet.iterator().next();
        assertEquals("BRAF", hotspot.sourceEvent());
        assertEquals(Knowledgebase.CKB_TRIAL, hotspot.source());
        assertEquals("nctid", hotspot.treatment().name());
        assertEquals("AB", hotspot.applicableCancerType().name());
        assertEquals("162", hotspot.applicableCancerType().doid());
        assertTrue(hotspot.blacklistCancerTypes().isEmpty());
        assertEquals(EvidenceLevel.B, characteristics.level());
        assertEquals(EvidenceDirection.RESPONSIVE, characteristics.direction());
        assertEquals("Netherlands", characteristics.evidenceUrls().iterator().next());
    }

    @Test
    public void canExtractCancerTypeDetails() {
        assertNull(ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:not a doid")));

        assertEquals("0060463",
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "DOID:0060463"))
                        .applicableCancerType()
                        .doid());
        assertEquals(CancerTypeConstants.CANCER_DOID,
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000003"))
                        .applicableCancerType()
                        .doid());
        assertEquals(CancerTypeConstants.SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY,
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000009"))
                        .applicableCancerType()
                        .doid());
        assertEquals(CancerTypeConstants.ADENOCARCINOMA_OF_UNKNOWN_PRIMARY,
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000008"))
                        .applicableCancerType()
                        .doid());
        assertNull(ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000004")));

        assertEquals(Sets.newHashSet(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE,
                        CancerTypeConstants.BONE_MARROW_TYPE,
                        CancerTypeConstants.LEUKEMIA_TYPE),
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000003"))
                        .blacklistedCancerTypes());
        assertEquals(Sets.newHashSet(),
                ActionableFunctions.extractCancerTypeDetails(CkbTrialTestFactory.createIndication("test", "JAX:10000009"))
                        .blacklistedCancerTypes());

    }

    @Test
    public void canExtractSourceCancerTypeID() {
        assertNull(ActionableFunctions.extractSourceCancerTypeId(null));
        assertNull(ActionableFunctions.extractSourceCancerTypeId("not a doid"));

        assertNotNull(ActionableFunctions.extractSourceCancerTypeId("DOID:0060463"));
        assertEquals("0060463", ActionableFunctions.extractSourceCancerTypeId("DOID:0060463")[1]);
        assertEquals("10000003", ActionableFunctions.extractSourceCancerTypeId("JAX:10000003")[1]);
    }

    @Test
    public void canDetermineIfHasVariantRequirementTypeToInclude() {
        ClinicalTrial trialWithRequiredType =
                CkbTrialTestFactory.trialWithRequirementType(Lists.newArrayList(ImmutableVariantRequirementDetail.builder()
                        .profileId(0)
                        .requirementType("required")
                        .build()));
        ClinicalTrial trialWithExcludedType =
                CkbTrialTestFactory.trialWithRequirementType(Lists.newArrayList(ImmutableVariantRequirementDetail.builder()
                        .profileId(0)
                        .requirementType("excluded")
                        .build()));
        CkbEntry entry =
                CkbTrialTestFactory.createEntry("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        assertTrue(ActionableTrialFactory.hasVariantRequirementTypeToInclude(trialWithRequiredType.variantRequirementDetails(), entry));
        assertFalse(ActionableTrialFactory.hasVariantRequirementTypeToInclude(trialWithExcludedType.variantRequirementDetails(), entry));
    }

    @Test
    public void canDetermineIfHasCountryToIncludeWithPotentiallyOpenRecruitmentType() {
        ClinicalTrial notOpenDutchTrial =
                CkbTrialTestFactory.trialWithCountryAndRecruitmentType(Lists.newArrayList(ImmutableLocation.builder()
                        .nctId("")
                        .city("")
                        .country("Netherlands")
                        .status("Not yet recruiting")
                        .build()), "Recruiting");
        ClinicalTrial OpenDutchTrial = CkbTrialTestFactory.trialWithCountryAndRecruitmentType(Lists.newArrayList(ImmutableLocation.builder()
                .nctId("")
                .city("")
                .country("Netherlands")
                .status("Recruiting")
                .build()), "Recruiting");
        ClinicalTrial americanTrial = CkbTrialTestFactory.trialWithCountryAndRecruitmentType(Lists.newArrayList(ImmutableLocation.builder()
                .nctId("")
                .city("")
                .country("United States")
                .status("Recruiting")
                .build()), "Recruiting");
        assertFalse(ActionableTrialFactory.hasCountryToIncludeWithPotentiallyOpenRecruitmentType(notOpenDutchTrial));
        assertTrue(ActionableTrialFactory.hasCountryToIncludeWithPotentiallyOpenRecruitmentType(OpenDutchTrial));
        assertFalse(ActionableTrialFactory.hasCountryToIncludeWithPotentiallyOpenRecruitmentType(americanTrial));
    }
}