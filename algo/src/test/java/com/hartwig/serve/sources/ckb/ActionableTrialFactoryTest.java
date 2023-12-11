package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
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

import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableTrialFactoryTest {

    @Test
    public void canCreateActionableTrial() {
        Location location = ImmutableLocation.builder().nctId("nctid").city("").country("Netherlands").status("Recruiting").build();
        VariantRequirementDetail requirementType =
                ImmutableVariantRequirementDetail.builder().profileId(0).requirementType("required").build();
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(location, "Recruiting", requirementType);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory();
        Set<ActionableEntry> entrySet = actionableTrialFactory.create(entry, "KRAS", "gene");

        assertEquals(1, entrySet.size());
        ActionableEntry trial = entrySet.iterator().next();
        assertEquals(Knowledgebase.CKB_TRIAL, trial.source());
        assertEquals("KRAS", trial.sourceEvent());
        assertEquals(Sets.newHashSet("https://clinicaltrials.gov/study/nctid"), trial.sourceUrls());
        assertEquals("nctid", trial.treatment().name());
        assertEquals("AB", trial.applicableCancerType().name());
        assertEquals("162", trial.applicableCancerType().doid());
        assertEquals(Sets.newHashSet(), trial.blacklistCancerTypes());
        assertEquals(EvidenceLevel.B, trial.level());
        assertEquals(EvidenceDirection.RESPONSIVE, trial.direction());
        assertEquals(Sets.newHashSet("Netherlands"), trial.evidenceUrls());
    }

    @Test
    public void canDetermineCountriesToInclude() {
        ClinicalTrial trialPotentiallyOpenInBelgium = createTrial("Recruiting", "Belgium", "Recruiting");
        ClinicalTrial trialNotOpenInNetherlands = createTrial("Recruiting", "Netherlands", "Not yet recruiting");
        ClinicalTrial trialPotentiallyOpenInUnitedStates = createTrial("Recruiting", "United States", "Recruiting");
        ClinicalTrial trialWithdrawnInUnitedStates = createTrial("Recruiting", "United States", "Withdrawn");
        ClinicalTrial trialPotentiallyOpenInBelgiumAndIndia =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "India", "Recruiting");
        ClinicalTrial trialPotentiallyOpenInBelgiumAndGermany =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "Germany", "Active, not recruiting");
        ClinicalTrial trialPotentiallyOpenInBelgiumSuspendedInGermany =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "Germany", "Suspended");
        ClinicalTrial trialPotentiallyOpenInBelgiumSuspendedInIndia =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "India", "Suspended");
        ClinicalTrial terminatedTrial = createTrial("Terminated", "Netherlands", "Suspended");
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgium).size());
        assertEquals(0, ActionableTrialFactory.countriesToInclude(trialNotOpenInNetherlands).size());
        assertEquals(0, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInUnitedStates).size());
        assertEquals(0, ActionableTrialFactory.countriesToInclude(trialWithdrawnInUnitedStates).size());
        assertEquals(0, ActionableTrialFactory.countriesToInclude(terminatedTrial).size());
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumAndIndia).size());
        assertEquals(2, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumAndGermany).size());
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumSuspendedInGermany).size());
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumSuspendedInIndia).size());
    }

    @Test
    public void canDetermineIfHasVariantRequirementTypeToInclude() {
        assertTrue(ActionableTrialFactory.hasVariantRequirementTypeToInclude(extractVariantRequirementDetails("required"),
                createBasicEntry()));
        assertTrue(ActionableTrialFactory.hasVariantRequirementTypeToInclude(extractVariantRequirementDetails("partial - required"),
                createBasicEntry()));
        assertFalse(ActionableTrialFactory.hasVariantRequirementTypeToInclude(extractVariantRequirementDetails("excluded"),
                createBasicEntry()));
        assertFalse(ActionableTrialFactory.hasVariantRequirementTypeToInclude(extractVariantRequirementDetails("diagnostic"),
                createBasicEntry()));
        assertFalse(ActionableTrialFactory.hasVariantRequirementTypeToInclude(extractVariantRequirementDetails("partial - excluded"),
                createBasicEntry()));
        assertFalse(ActionableTrialFactory.hasVariantRequirementTypeToInclude(extractVariantRequirementDetails("allowed"),
                createBasicEntry()));
    }

    @NotNull
    private static CkbEntry createBasicEntry() {
        return CkbTestFactory.createEntry("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162");
    }

    @NotNull
    private static List<VariantRequirementDetail> extractVariantRequirementDetails(@NotNull String requirementType) {
        ClinicalTrial trial = CkbTestFactory.createTrialWithRequirementType(Lists.newArrayList(ImmutableVariantRequirementDetail.builder()
                .profileId(0)
                .requirementType(requirementType)
                .build()));
        return trial.variantRequirementDetails();
    }

    @NotNull
    private static ClinicalTrial createTrial(@NotNull String recruitmentTrial, @NotNull String country,
            @NotNull String recruitmentCountry) {
        return CkbTestFactory.createTrialWithCountryAndRecruitmentType(Lists.newArrayList(ImmutableLocation.builder()
                .nctId("")
                .city("")
                .country(country)
                .status(recruitmentCountry)
                .build()), recruitmentTrial);
    }

    @NotNull
    private static ClinicalTrial createTrialWithMultipleLocations(@NotNull String recruitmentTrial, @NotNull String country1,
            @NotNull String recruitmentCountry1, @NotNull String country2, @NotNull String recruitmentCountry2) {
        return CkbTestFactory.createTrialWithCountryAndRecruitmentType(Lists.newArrayList(ImmutableLocation.builder()
                        .nctId("")
                        .city("")
                        .country(country1)
                        .status(recruitmentCountry1)
                        .build(), ImmutableLocation.builder().nctId("").city("").country(country2).status(recruitmentCountry2).build()),
                recruitmentTrial);
    }
}