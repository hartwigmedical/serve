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
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistStudyTest;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistStudyType;
import com.hartwig.serve.sources.ckb.blacklist.CkbStudyBlacklistModel;

import org.apache.commons.compress.utils.Sets;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableTrialFactoryTest {

    private static final CkbStudyBlacklistModel BLACKLIST_MODEL = CkbBlacklistStudyTest.createCkbBlacklistStudies();

    @Test
    public void canCreateActionableEntryForOpenTrialInAllowedCountryWithRequiredMolecularProfile() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial",
                        List.of(CkbTestFactory.createTherapy("Nivolumab")), List.of(CkbTestFactory.createIndication("test", "JAX:10000006")));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(BLACKLIST_MODEL);
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");

        assertEquals(1, trials.size());
        ActionableEntry trial = trials.iterator().next();
        com.hartwig.serve.datamodel.ClinicalTrial clinicalTrial1 = DatamodelTestFactory.clinicalTrialBuilder(trial);

        assertEquals(Knowledgebase.CKB_TRIAL, trial.source());
        assertEquals("KRAS", trial.sourceEvent());
        assertEquals(Sets.newHashSet("https://ckbhome.jax.org/clinicalTrial/show?nctId=NCT0102"), trial.sourceUrls());
        assertEquals("Phase I trial", clinicalTrial1.studyTitle());
        assertEquals(EvidenceLevel.B, trial.level());
        assertEquals(EvidenceDirection.RESPONSIVE, trial.direction());
        assertEquals(Sets.newHashSet("https://clinicaltrials.gov/study/NCT0102"), trial.evidenceUrls());
        assertEquals(Sets.newHashSet("Netherlands"), clinicalTrial1.countriesOfStudy());
    }

    @Test
    public void canCBlacklistStudies() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0456", "Phase I trial",
                        List.of(CkbTestFactory.createTherapy("Nivolumab")), List.of(CkbTestFactory.createIndication("test", "JAX:10000006")));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbStudyBlacklistModel model = CkbBlacklistStudyTest.defineBlacklistStudies(CkbBlacklistStudyType.STUDY_WHOLE,
                "NCT0456", null, null, null, null);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(model);
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");
        assertEquals(0, trials.size());
    }

    @Test
    public void canNotBlacklistStudies() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0456", "Phase I trial",
                        List.of(CkbTestFactory.createTherapy("Nivolumab")), List.of(CkbTestFactory.createIndication("test", "JAX:10000006")));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbStudyBlacklistModel model = CkbBlacklistStudyTest.defineBlacklistStudies(CkbBlacklistStudyType.STUDY_WHOLE,
                "NCT123", null, null, null, null);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(model);
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");
        assertEquals(1, trials.size());
    }

    @Test
    public void canBlacklistOnGene() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0456", "Phase I trial",
                        List.of(CkbTestFactory.createTherapy("Nivolumab")), List.of(CkbTestFactory.createIndication("test", "JAX:10000006")));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbStudyBlacklistModel model = CkbBlacklistStudyTest.defineBlacklistStudies(CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE,
                null, null, null, "EGFR", null);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(model);
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "EGFR", "EGFR");
        assertEquals(0, trials.size());
    }

    @Test
    public void canNotBlacklistOnGene() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTerapy("Recruiting", List.of(requirementDetail), List.of(location), "NCT0456", "Phase I trial",
                        List.of(CkbTestFactory.createTherapy("Nivolumab")), List.of(CkbTestFactory.createIndication("test", "JAX:10000006")));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbStudyBlacklistModel model = CkbBlacklistStudyTest.defineBlacklistStudies(CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE,
                null, null, null, "ATM", null);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(model);
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "EGFR", "EGFR");
        assertEquals(1, trials.size());
    }



    @Test
    public void shouldNotCreateAnActionableEntryWhenVariantRequirementIsOnADifferentProfile() {
        Location location = CkbTestFactory.createLocation("Belgium", "Recruiting");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(0, "required");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrial("Recruiting", List.of(requirementDetail), List.of(location), "NCT0102", "Phase I trial");
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(1, Strings.EMPTY, clinicalTrial);

        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(BLACKLIST_MODEL);
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");

        assertEquals(0, trials.size());
    }

    @Test
    public void canDetermineCountriesToInclude() {
        ClinicalTrial trialPotentiallyOpenInBelgium = createTrialWithOneLocation("Recruiting", "Belgium", "Recruiting");
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgium).size());

        ClinicalTrial trialNotOpenInNetherlands = createTrialWithOneLocation("Recruiting", "Netherlands", "Completed");
        assertEquals(0, ActionableTrialFactory.countriesToInclude(trialNotOpenInNetherlands).size());

        ClinicalTrial trialPotentiallyOpenInUnitedStates = createTrialWithOneLocation("Recruiting", "United States", "Recruiting");
        assertEquals(0, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInUnitedStates).size());

        ClinicalTrial trialWithdrawnInUnitedStates = createTrialWithOneLocation("Recruiting", "United States", "Withdrawn");
        assertEquals(0, ActionableTrialFactory.countriesToInclude(trialWithdrawnInUnitedStates).size());

        ClinicalTrial trialPotentiallyOpenInBelgiumAndIndia =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "India", "Recruiting");
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumAndIndia).size());

        ClinicalTrial trialPotentiallyOpenInBelgiumAndGermany =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "Germany", "Not yet recruiting");
        assertEquals(2, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumAndGermany).size());

        ClinicalTrial trialPotentiallyOpenInBelgiumSuspendedInGermany =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "Germany", "Suspended");
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumSuspendedInGermany).size());

        ClinicalTrial trialPotentiallyOpenInBelgiumSuspendedInIndia =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "India", "Suspended");
        assertEquals(1, ActionableTrialFactory.countriesToInclude(trialPotentiallyOpenInBelgiumSuspendedInIndia).size());

        ClinicalTrial trialSuspendedInBelgiumOpenInIndia =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Suspended", "India", "Not yet recruiting");
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