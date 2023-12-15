package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
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
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(0, "required");
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrialDetails("KRAS",
                "BRAF V600E",
                "BRAF V600E",
                0,
                location,
                "Recruiting",
                requirementDetail,
                "NCT0102",
                "Phase I trial");
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory();
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");

        assertEquals(1, trials.size());
        ActionableEntry trial = trials.iterator().next();
        assertEquals(Knowledgebase.CKB_TRIAL, trial.source());
        assertEquals("KRAS", trial.sourceEvent());
        assertEquals(Sets.newHashSet("https://clinicaltrials.gov/study/NCT0102"), trial.sourceUrls());
        assertEquals("Phase I trial", trial.treatment().name());
        assertEquals(EvidenceLevel.B, trial.level());
        assertEquals(EvidenceDirection.RESPONSIVE, trial.direction());
        assertEquals(Sets.newHashSet("Netherlands"), trial.evidenceUrls());
    }

    @Test
    public void shouldNotCreateAnActionableEntryWhenVariantRequirementIsOnADifferentProfile() {
        Location location = CkbTestFactory.createLocation("Belgium", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(0, "required");
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrialDetails("KRAS",
                "BRAF V600E",
                "BRAF V600E",
                1,
                location,
                "Recruiting",
                requirementDetail,
                "NCT0102",
                "Phase I trial");
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory();
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");

        assertEquals(0, trials.size());
    }

    @Test
    public void canDetermineCountriesToInclude() {
        ClinicalTrial trialPotentiallyOpenInBelgium = createTrialWithOneLocation("Recruiting", "Belgium", "Recruiting");
        ClinicalTrial trialNotOpenInNetherlands = createTrialWithOneLocation("Recruiting", "Netherlands", "Not yet recruiting");
        ClinicalTrial trialPotentiallyOpenInUnitedStates = createTrialWithOneLocation("Recruiting", "United States", "Recruiting");
        ClinicalTrial trialWithdrawnInUnitedStates = createTrialWithOneLocation("Recruiting", "United States", "Withdrawn");
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
        ClinicalTrial terminatedTrial = createTrialWithOneLocation("Terminated", "Netherlands", "Suspended");
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
        return ActionableTrialFactory.hasVariantRequirementTypeToInclude(List.of(CkbTestFactory.createVariantRequirementDetail(0,
                        requirementType)),
                CkbTestFactory.createEntryWithOpenMolecularTrial("BRAF", "BRAF V600E", "BRAF V600E", "sensitive", "Actionable"));
    }

    @NotNull
    private static ClinicalTrial createTrialWithMultipleLocations(@NotNull String recruitmentTrial, @NotNull String country1,
            @NotNull String recruitmentCountry1, @NotNull String country2, @NotNull String recruitmentCountry2) {
        return CkbTestFactory.createTrial(recruitmentTrial,
                List.of(CkbTestFactory.createVariantRequirementDetail(0, "required")),
                List.of(CkbTestFactory.createLocation(country1, recruitmentCountry1),
                        CkbTestFactory.createLocation(country2, recruitmentCountry2)),
                "nctid",
                "title");
    }

    @NotNull
    private static ClinicalTrial createTrialWithOneLocation(@NotNull String recruitmentTrial, @NotNull String country,
            @NotNull String recruitmentCountry) {
        return CkbTestFactory.createTrial(recruitmentTrial,
                List.of(CkbTestFactory.createVariantRequirementDetail(0, "required")),
                List.of(CkbTestFactory.createLocation(country, recruitmentCountry)),
                "nctid",
                "title");
    }
}