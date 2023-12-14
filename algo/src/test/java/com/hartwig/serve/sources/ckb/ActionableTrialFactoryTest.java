package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
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
    public void canCreateActionableEntryForOpenTrialInAllowedCountryWithRequiredMolecularProfile() {
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementType =
                ImmutableVariantRequirementDetail.builder().profileId(0).requirementType("required").build();
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(0, location, "Recruiting", requirementType);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory();
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");

        assertEquals(1, trials.size());
        ActionableEntry trial = trials.iterator().next();
        assertEquals(Knowledgebase.CKB_TRIAL, trial.source());
        assertEquals("KRAS", trial.sourceEvent());
        assertEquals(Sets.newHashSet("https://clinicaltrials.gov/study/nctid"), trial.sourceUrls());
        assertEquals("title", trial.treatment().name());
        assertEquals(EvidenceLevel.B, trial.level());
        assertEquals(EvidenceDirection.RESPONSIVE, trial.direction());
        assertEquals(Sets.newHashSet("Netherlands"), trial.evidenceUrls());
    }

    @Test
    public void shouldNotCreateAnActionableEntryWhenVariantRequirementIsOnADifferentProfile() {
        Location location = CkbTestFactory.createLocation("Belgium", "Recruiting");
        VariantRequirementDetail requirementType =
                ImmutableVariantRequirementDetail.builder().profileId(0).requirementType("required").build();
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(1, location, "Recruiting", requirementType);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory();
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");

        assertEquals(0, trials.size());
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
        ClinicalTrial trialSuspendedInBelgiumOpenInIndia =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Suspended", "India", "Active, not recruiting");
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
        assertEquals(0, ActionableTrialFactory.countriesToInclude(trialSuspendedInBelgiumOpenInIndia).size());
    }

    @Test
    public void canDetermineIfHasVariantRequirementTypeToInclude() {
        assertTrue(hasRequirementTypeToInclude("required"));
        assertTrue(hasRequirementTypeToInclude("partial - required"));
        assertFalse(hasRequirementTypeToInclude("excluded"));
        assertFalse(hasRequirementTypeToInclude("diagnostic"));
        assertFalse(hasRequirementTypeToInclude("partial - excluded"));
        assertFalse(hasRequirementTypeToInclude("allowed"));
    }

    private static boolean hasRequirementTypeToInclude(@NotNull String requirementType) {
        return ActionableTrialFactory.hasVariantRequirementTypeToInclude(createVariantRequirementDetails(requirementType),
                CkbTestFactory.createEntry("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable", "AB", "AB", "A", "DOID:162"));
    }

    @NotNull
    private static List<VariantRequirementDetail> createVariantRequirementDetails(@NotNull String requirementType) {
        return Lists.newArrayList(ImmutableVariantRequirementDetail.builder().profileId(0).requirementType(requirementType).build());
    }

    @NotNull
    private static ClinicalTrial createTrial(@NotNull String recruitmentTrial, @NotNull String country,
            @NotNull String recruitmentCountry) {
        return CkbTestFactory.createTrialWithCountryAndRecruitmentType(Lists.newArrayList(CkbTestFactory.createLocation(country,
                recruitmentCountry)), recruitmentTrial);
    }

    @NotNull
    private static ClinicalTrial createTrialWithMultipleLocations(@NotNull String recruitmentTrial, @NotNull String country1,
            @NotNull String recruitmentCountry1, @NotNull String country2, @NotNull String recruitmentCountry2) {
        return CkbTestFactory.createTrialWithCountryAndRecruitmentType(Lists.newArrayList(CkbTestFactory.createLocation(country1,
                recruitmentCountry1), CkbTestFactory.createLocation(country2, recruitmentCountry2)), recruitmentTrial);
    }
}