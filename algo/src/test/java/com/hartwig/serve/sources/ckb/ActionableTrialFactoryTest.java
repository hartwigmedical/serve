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
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableTrialFactoryTest {

    @Test
    public void canCreateActionableEntryForOpenTrialInAllowedCountryWithRequiredMolecularProfile() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrial("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial");
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

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
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrial("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial");
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(1, Strings.EMPTY, clinicalTrial);

        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory();
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");

        assertEquals(0, trials.size());
    }

    @Test
    public void canDetermineCountriesToInclude() {
        ClinicalTrial trialPotentiallyOpenInBelgium = createTrialWithOneLocation("Recruiting", "Belgium", "Recruiting");
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgium).size());

        ClinicalTrial trialNotOpenInNetherlands = createTrialWithOneLocation("Recruiting", "Netherlands", "Not yet recruiting");
        assertEquals(0, ActionableTrialFactory.countriesToInclude(trialNotOpenInNetherlands).size());

        ClinicalTrial trialPotentiallyOpenInUnitedStates = createTrialWithOneLocation("Recruiting", "United States", "Recruiting");
        assertEquals(0, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInUnitedStates).size());

        ClinicalTrial trialWithdrawnInUnitedStates = createTrialWithOneLocation("Recruiting", "United States", "Withdrawn");
        assertEquals(0, ActionableTrialFactory.countriesToInclude(trialWithdrawnInUnitedStates).size());

        ClinicalTrial trialPotentiallyOpenInBelgiumAndIndia =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "India", "Recruiting");
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumAndIndia).size());

        ClinicalTrial trialPotentiallyOpenInBelgiumAndGermany =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "Germany", "Active, not recruiting");
        assertEquals(2, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumAndGermany).size());

        ClinicalTrial trialPotentiallyOpenInBelgiumSuspendedInGermany =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "Germany", "Suspended");
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumSuspendedInGermany).size());

        ClinicalTrial trialPotentiallyOpenInBelgiumSuspendedInIndia =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "India", "Suspended");
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumSuspendedInIndia).size());

        ClinicalTrial trialSuspendedInBelgiumOpenInIndia =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Suspended", "India", "Active, not recruiting");
        assertEquals(0, ActionableTrialFactory.countriesToInclude(trialSuspendedInBelgiumOpenInIndia).size());

        ClinicalTrial terminatedTrial = createTrialWithOneLocation("Terminated", "Netherlands", "Suspended");
        assertEquals(0, ActionableTrialFactory.countriesToInclude(terminatedTrial).size());
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
        CkbEntry baseEntry = CkbTestFactory.createEntryWithGene("gene");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(baseEntry.profileId(), requirementType);

        return ActionableTrialFactory.hasVariantRequirementTypeToInclude(List.of(requirementDetail), baseEntry);
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