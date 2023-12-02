package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableLocation;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ImmutableVariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;

public class ActionableTrialFactoryTest {

    @Test
    public void canCreateActionableTrials() {
        Location location = ImmutableLocation.builder().nctId("").city("").country("United States").status("Recruiting").build();
        VariantRequirementDetail requirementType =
                ImmutableVariantRequirementDetail.builder().profileId(0).requirementType("required").build();
        CkbEntry entryDeletion = CkbTestFactory.createEntryWithClinicalTrial("KRAS",
                "deletion",
                "KRAS deletion",
                "sensitive",
                "Emerging",
                "AB",
                "AB",
                "A",
                "DOID:162",
                location,
                "Recruiting",
                requirementType);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory();
        Set<ActionableEntry> entryDeletionSet = actionableTrialFactory.create(entryDeletion, "KRAS", "gene");
        assertEquals(0, entryDeletionSet.size());

        CkbEntry entryCharacteristics =
                CkbTestFactory.createEntry("-", "MSI neg", "MSI neg", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        ActionableTrialFactory actionableTrialFactoryCharacteristic = new ActionableTrialFactory();
        Set<ActionableEntry> entryCharacteristicsSet =
                actionableTrialFactoryCharacteristic.create(entryCharacteristics, Strings.EMPTY, "-");
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

        CkbEntry entryAmplification = CkbTestFactory.createEntry("KRAS",
                "KRAS amplification",
                "KRAS amplification",
                "sensitive",
                "Actionable",
                "AB",
                "AB",
                "A",
                "DOID:163");
        ActionableTrialFactory actionableTrialFactoryAmplification = new ActionableTrialFactory();
        Set<ActionableEntry> trialAmplificationSet = actionableTrialFactoryAmplification.create(entryAmplification, "KRAS", "KRAS");
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
                CkbTestFactory.createEntry("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        ActionableTrialFactory actionableTrialFactoryHotspot = new ActionableTrialFactory();
        Set<ActionableEntry> entryHotspotSet = actionableTrialFactoryHotspot.create(entryHotspot, "BRAF", "BRAF");
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
    public void canDetermineIfHasVariantRequirementTypeToInclude() {
        ClinicalTrial trialWithRequiredType =
                CkbTestFactory.createTrialWithRequirementType(Lists.newArrayList(ImmutableVariantRequirementDetail.builder()
                        .profileId(0)
                        .requirementType("required")
                        .build()));
        ClinicalTrial trialWithExcludedType =
                CkbTestFactory.createTrialWithRequirementType(Lists.newArrayList(ImmutableVariantRequirementDetail.builder()
                        .profileId(0)
                        .requirementType("excluded")
                        .build()));
        CkbEntry entry =
                CkbTestFactory.createEntry("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
        assertTrue(ActionableTrialFactory.hasVariantRequirementTypeToInclude(trialWithRequiredType.variantRequirementDetails(), entry));
        assertFalse(ActionableTrialFactory.hasVariantRequirementTypeToInclude(trialWithExcludedType.variantRequirementDetails(), entry));
    }

    @Test
    public void canDetermineIfHasCountryToIncludeWithPotentiallyOpenRecruitmentType() {
        ClinicalTrial notOpenDutchTrial =
                CkbTestFactory.createTrialWithCountryAndRecruitmentType(Lists.newArrayList(ImmutableLocation.builder()
                        .nctId("")
                        .city("")
                        .country("Netherlands")
                        .status("Not yet recruiting")
                        .build()), "Recruiting");
        ClinicalTrial OpenDutchTrial =
                CkbTestFactory.createTrialWithCountryAndRecruitmentType(Lists.newArrayList(ImmutableLocation.builder()
                        .nctId("")
                        .city("")
                        .country("Netherlands")
                        .status("Recruiting")
                        .build()), "Recruiting");
        ClinicalTrial americanTrial = CkbTestFactory.createTrialWithCountryAndRecruitmentType(Lists.newArrayList(ImmutableLocation.builder()
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