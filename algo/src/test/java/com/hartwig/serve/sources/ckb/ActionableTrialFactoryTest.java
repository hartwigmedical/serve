package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableCountry;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistStudyEntry;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistStudyType;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistTestFactory;
import com.hartwig.serve.sources.ckb.blacklist.CkbStudyBlacklistModel;
import com.hartwig.serve.sources.ckb.blacklist.ImmutableCkbBlacklistStudyEntry;
import com.hartwig.serve.sources.ckb.region.CkbRegion;
import com.hartwig.serve.sources.ckb.region.ImmutableCkbRegion;

import org.apache.commons.compress.utils.Sets;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class ActionableTrialFactoryTest {

    private static final CkbStudyBlacklistModel BLACKLIST_MODEL = CkbBlacklistTestFactory.createProperStudyBlacklist();
    private static final Set<CkbRegion> REGIONS_TO_INCLUDE =
            Set.of(createRegion("netherlands"), createRegion("belgium"), createRegion("germany"), createRegion("united states", "maine"));

    @Test
    public void canCreateActionableEntryForOpenTrialInAllowedCountryWithRequiredMolecularProfileAndValidAgeGroup() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting", "Rotterdam", "EMC");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial = CkbTestFactory.createTrialWithTherapy("Recruiting",
                List.of(requirementDetail),
                List.of(location),
                "NCT0102",
                "Phase I trial",
                List.of(CkbTestFactory.createTherapy("Nivolumab")),
                List.of(CkbTestFactory.createIndication("test", "JAX:10000006")),
                List.of("senior", "child", "adult"));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(BLACKLIST_MODEL, REGIONS_TO_INCLUDE);
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");

        assertEquals(1, trials.size());
        ActionableEntry trial = trials.iterator().next();
        com.hartwig.serve.datamodel.ClinicalTrial clinicalTrial1 = DatamodelTestFactory.extractClinicalTrial(trial);

        assertEquals(Knowledgebase.CKB_TRIAL, trial.source());
        assertEquals("KRAS", trial.sourceEvent());
        assertEquals(Sets.newHashSet("https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=1"),
                trial.sourceUrls());
        assertEquals("Phase I trial", clinicalTrial1.title());
        assertEquals(EvidenceLevel.B, trial.evidenceLevel());
        assertEquals(EvidenceDirection.RESPONSIVE, trial.direction());
        assertEquals(Sets.newHashSet("https://clinicaltrials.gov/study/NCT0102"), trial.evidenceUrls());
        assertEquals(Sets.newHashSet(ImmutableCountry.builder()
                .countryName("Netherlands")
                .hospitalsPerCity(Map.of("Rotterdam", Sets.newHashSet("EMC")))
                .build()), clinicalTrial1.countries());
    }

    @Test
    public void canBlacklistStudies() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting", "Groningen", "UMCG");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial = CkbTestFactory.createTrialWithTherapy("Recruiting",
                List.of(requirementDetail),
                List.of(location),
                "NCT0456",
                "Phase I trial",
                List.of(CkbTestFactory.createTherapy("Nivolumab")),
                List.of(CkbTestFactory.createIndication("test", "JAX:10000006")),
                List.of("senior", "child", "adult"));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbStudyBlacklistModel model = createBlacklistModel(CkbBlacklistStudyType.STUDY_WHOLE, "NCT0456", null, null, null, null);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(model, REGIONS_TO_INCLUDE);
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");
        assertEquals(0, trials.size());
    }

    @Test
    public void doesNotBlacklistStudies() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting", "Groningen", "UMCG");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial = CkbTestFactory.createTrialWithTherapy("Recruiting",
                List.of(requirementDetail),
                List.of(location),
                "NCT0456",
                "Phase I trial",
                List.of(CkbTestFactory.createTherapy("Nivolumab")),
                List.of(CkbTestFactory.createIndication("test", "JAX:10000006")),
                List.of("senior", "child", "adult"));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbStudyBlacklistModel model = createBlacklistModel(CkbBlacklistStudyType.STUDY_WHOLE, "NCT123", null, null, null, null);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(model, REGIONS_TO_INCLUDE);
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");
        assertEquals(1, trials.size());
    }

    @Test
    public void canBlacklistOnGene() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting", "Groningen", "UMCG");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial = CkbTestFactory.createTrialWithTherapy("Recruiting",
                List.of(requirementDetail),
                List.of(location),
                "NCT0456",
                "Phase I trial",
                List.of(CkbTestFactory.createTherapy("Nivolumab")),
                List.of(CkbTestFactory.createIndication("test", "JAX:10000006")),
                List.of("senior", "child", "adult"));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbStudyBlacklistModel model =
                createBlacklistModel(CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE, null, null, null, "EGFR", null);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(model, REGIONS_TO_INCLUDE);
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "EGFR", "EGFR");
        assertEquals(0, trials.size());
    }

    @Test
    public void doesNotBlacklistOnGene() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting", "Groningen", "UMCG");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial = CkbTestFactory.createTrialWithTherapy("Recruiting",
                List.of(requirementDetail),
                List.of(location),
                "NCT0456",
                "Phase I trial",
                List.of(CkbTestFactory.createTherapy("Nivolumab")),
                List.of(CkbTestFactory.createIndication("test", "JAX:10000006")),
                List.of("senior", "child", "adult"));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbStudyBlacklistModel model = createBlacklistModel(CkbBlacklistStudyType.ALL_STUDIES_BASED_ON_GENE, null, null, null, "ATM", null);
        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(model, REGIONS_TO_INCLUDE);
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "EGFR", "EGFR");
        assertEquals(1, trials.size());
    }

    @Test
    public void shouldNotCreateAnActionableEntryWhenVariantRequirementIsOnADifferentProfile() {
        Location location = CkbTestFactory.createLocation("Belgium", "Recruiting", "Brussel", "UZ Brussel");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(0, "required");
        ClinicalTrial clinicalTrial = CkbTestFactory.createTrial("Recruiting",
                List.of(requirementDetail),
                List.of(location),
                "NCT0102",
                "Phase I trial",
                List.of("senior", "child", "adult"));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(1, Strings.EMPTY, clinicalTrial);

        ActionableTrialFactory actionableTrialFactory = new ActionableTrialFactory(BLACKLIST_MODEL, new HashSet<>());
        Set<ActionableEntry> trials = actionableTrialFactory.create(entry, "KRAS", "gene");

        assertEquals(0, trials.size());
    }

    @Test
    public void canDetermineCountriesAndStatesToInclude() {
        ClinicalTrial trialPotentiallyOpenInBelgium = createTrialWithOneLocation("Recruiting", "Belgium", "Brussel", "Recruiting", null);
        assertEquals(1, ActionableTrialFactory.extractCountriesToInclude(trialPotentiallyOpenInBelgium, REGIONS_TO_INCLUDE).size());

        ClinicalTrial trialNotOpenInNetherlands = createTrialWithOneLocation("Recruiting", "Netherlands", "Rotterdam", "Completed", null);
        assertEquals(0, ActionableTrialFactory.extractCountriesToInclude(trialNotOpenInNetherlands, REGIONS_TO_INCLUDE).size());

        ClinicalTrial trialPotentiallyOpenInUSMaine =
                createTrialWithOneLocation("Recruiting", "United States", "Augusta", "Recruiting", "Maine", null);
        assertEquals(1, ActionableTrialFactory.extractCountriesToInclude(trialPotentiallyOpenInUSMaine, REGIONS_TO_INCLUDE).size());

        ClinicalTrial trialPotentiallyOpenInUSCalifornia =
                createTrialWithOneLocation("Recruiting", "United States", "Los Angeles", "Recruiting", "California", null);
        assertEquals(0, ActionableTrialFactory.extractCountriesToInclude(trialPotentiallyOpenInUSCalifornia, REGIONS_TO_INCLUDE).size());

        ClinicalTrial trialPotentiallyOpenInCanada = createTrialWithOneLocation("Recruiting", "Canada", "Toronto", "Recruiting", null);
        assertEquals(0, ActionableTrialFactory.extractCountriesToInclude(trialPotentiallyOpenInCanada, REGIONS_TO_INCLUDE).size());

        ClinicalTrial trialWithdrawnInCanada = createTrialWithOneLocation("Recruiting", "Canada", "Toronto", "Withdrawn", null);
        assertEquals(0, ActionableTrialFactory.extractCountriesToInclude(trialWithdrawnInCanada, REGIONS_TO_INCLUDE).size());

        ClinicalTrial trialPotentiallyOpenInBelgiumAndIndia =
                createTrialWithMultipleLocations("Recruiting", "Belgium", "Recruiting", "Brussel", null, "India", "Recruiting", "Mumbai",
                null);
        assertEquals(1, ActionableTrialFactory.extractCountriesToInclude(trialPotentiallyOpenInBelgiumAndIndia, REGIONS_TO_INCLUDE).size());

        ClinicalTrial trialPotentiallyOpenInBelgiumAndGermany = createTrialWithMultipleLocations("Recruiting",
                "Belgium",
                "Recruiting",
                "Brussel",
                null,
                "Germany",
                "Not yet recruiting",
                "Koln",
                null);
        assertEquals(2,
                ActionableTrialFactory.extractCountriesToInclude(trialPotentiallyOpenInBelgiumAndGermany, REGIONS_TO_INCLUDE).size());

        ClinicalTrial trialPotentiallyOpenInBelgiumSuspendedInGermany = createTrialWithMultipleLocations("Recruiting",
                "Belgium",
                "Recruiting",
                "Brussel",
                null,
                "Germany",
                "Suspended",
                "Koln",
                null);
        assertEquals(1,
                ActionableTrialFactory.extractCountriesToInclude(trialPotentiallyOpenInBelgiumSuspendedInGermany, REGIONS_TO_INCLUDE)
                        .size());

        ClinicalTrial trialPotentiallyOpenInBelgiumSuspendedInIndia = createTrialWithMultipleLocations("Recruiting",
                "Belgium",
                "Recruiting",
                "Brussel",
                null,
                "India",
                "Suspended",
                "Mumbai",
                null);
        assertEquals(1,
                ActionableTrialFactory.extractCountriesToInclude(trialPotentiallyOpenInBelgiumSuspendedInIndia, REGIONS_TO_INCLUDE).size());

        ClinicalTrial trialSuspendedInBelgiumOpenInIndia = createTrialWithMultipleLocations("Recruiting",
                "Belgium",
                "Suspended",
                "Brussel",
                null,
                "India",
                "Not yet recruiting",
                "Mumbai",
                null);
        assertEquals(0, ActionableTrialFactory.extractCountriesToInclude(trialSuspendedInBelgiumOpenInIndia, REGIONS_TO_INCLUDE).size());

        ClinicalTrial terminatedTrial = createTrialWithOneLocation("Terminated", "Netherlands", "Rotterdam", "Suspended", null);
        assertEquals(0, ActionableTrialFactory.extractCountriesToInclude(terminatedTrial, REGIONS_TO_INCLUDE).size());
    }

    @Test
    public void canMapCities() {
        ClinicalTrial trialWithOneLocation =
                createTrialWithOneLocation("Recruiting", "Belgium", "Brussel", "Recruiting", "Unknown(Brussel)");
        assertEquals(Set.of(ImmutableCountry.builder()
                .countryName("Belgium")
                .hospitalsPerCity(Map.of("Brussel", Set.of("Unknown(Brussel)")))
                .build()), ActionableTrialFactory.extractCountriesToInclude(trialWithOneLocation, REGIONS_TO_INCLUDE));

        ClinicalTrial trialWithMultipleLocations = createTrialWithMultipleLocations("Recruiting",
                "Belgium",
                "Recruiting",
                "Brussel",
                "UZ Brussel",
                "Netherlands",
                "Not yet recruiting",
                "Rotterdam",
                "EMC");
        assertEquals(Set.of(ImmutableCountry.builder()
                                .countryName("Belgium")
                                .hospitalsPerCity(Map.of("Brussel", Set.of("UZ Brussel")))
                                .build(),
                        ImmutableCountry.builder().countryName("Netherlands").hospitalsPerCity(Map.of("Rotterdam", Set.of("EMC"))).build()),
                ActionableTrialFactory.extractCountriesToInclude(trialWithMultipleLocations, REGIONS_TO_INCLUDE));
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

    @Test
    public void hasAgeGroupToInclude() {
        assertTrue(ActionableTrialFactory.hasAgeGroupToInclude(List.of("senior")));
        assertTrue(ActionableTrialFactory.hasAgeGroupToInclude(List.of("adult")));
        assertFalse(ActionableTrialFactory.hasAgeGroupToInclude(List.of("child")));
        assertTrue(ActionableTrialFactory.hasAgeGroupToInclude(List.of("senior", "adult", "child")));
        assertTrue(ActionableTrialFactory.hasAgeGroupToInclude(List.of("senior", "child")));
        assertTrue(ActionableTrialFactory.hasAgeGroupToInclude(List.of("senior", "adult")));
        assertTrue(ActionableTrialFactory.hasAgeGroupToInclude(List.of("Senior")));
        assertTrue(ActionableTrialFactory.hasAgeGroupToInclude(List.of("Adult")));
        assertFalse(ActionableTrialFactory.hasAgeGroupToInclude(List.of("Child")));
        assertTrue(ActionableTrialFactory.hasAgeGroupToInclude(List.of("Senior", "Adult", "Child")));
        assertTrue(ActionableTrialFactory.hasAgeGroupToInclude(List.of("Senior", "Child")));
        assertTrue(ActionableTrialFactory.hasAgeGroupToInclude(List.of("Senior", "Adult")));
    }

    @Test
    public void hasPotentiallyOpenRequirementToInclude() {
        assertTrue(ActionableTrialFactory.hasPotentiallyOpenRequirementToInclude("recruiting"));
        assertTrue(ActionableTrialFactory.hasPotentiallyOpenRequirementToInclude("not yet recruiting"));
        assertTrue(ActionableTrialFactory.hasPotentiallyOpenRequirementToInclude("not_yet_recruiting"));
        assertTrue(ActionableTrialFactory.hasPotentiallyOpenRequirementToInclude("approved for marketing"));
        assertTrue(ActionableTrialFactory.hasPotentiallyOpenRequirementToInclude("available"));
        assertTrue(ActionableTrialFactory.hasPotentiallyOpenRequirementToInclude("enrolling by invitation"));
        assertTrue(ActionableTrialFactory.hasPotentiallyOpenRequirementToInclude("enrolling_by_invitation"));
        assertFalse(ActionableTrialFactory.hasPotentiallyOpenRequirementToInclude("unknown status"));
    }

    @NotNull
    private static ClinicalTrial createTrialWithMultipleLocations(@NotNull String recruitmentTrial, @NotNull String country1,
            @NotNull String recruitmentCountry1, @NotNull String city1, @Nullable String facility1, @NotNull String country2,
            @NotNull String recruitmentCountry2, @NotNull String city2, @Nullable String facility2) {
        return CkbTestFactory.createTrial(recruitmentTrial,
                List.of(CkbTestFactory.createVariantRequirementDetail(0, "required")),
                List.of(CkbTestFactory.createLocation(country1, recruitmentCountry1, city1, facility1),
                        CkbTestFactory.createLocation(country2, recruitmentCountry2, city2, facility2)),
                "nctid",
                "title",
                List.of("senior", "child", "adult"));
    }

    @NotNull
    private static ClinicalTrial createTrialWithOneLocation(@NotNull String recruitmentTrial, @NotNull String country, @NotNull String city,
            @NotNull String recruitmentCountry, @Nullable String facility) {
        return createTrialWithOneLocation(recruitmentTrial, country, city, recruitmentCountry, null, facility);
    }

    @NotNull
    private static ClinicalTrial createTrialWithOneLocation(@NotNull String recruitmentTrial, @NotNull String country, @NotNull String city,
            @NotNull String recruitmentCountry, @Nullable String state, @Nullable String facility) {
        return CkbTestFactory.createTrial(recruitmentTrial,
                List.of(CkbTestFactory.createVariantRequirementDetail(0, "required")),
                List.of(CkbTestFactory.createLocation(country, recruitmentCountry, city, state, facility)),
                "nctid",
                "title",
                List.of("senior", "child", "adult"));
    }

    @NotNull
    public static CkbStudyBlacklistModel createBlacklistModel(@NotNull CkbBlacklistStudyType type, @Nullable String nctId,
            @Nullable String therapy, @Nullable String cancerType, @Nullable String gene, @Nullable String event) {
        CkbBlacklistStudyEntry entry = ImmutableCkbBlacklistStudyEntry.builder()
                .type(type)
                .nctId(nctId)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .build();

        return CkbBlacklistTestFactory.createSpecificStudyBlacklist(entry);
    }

    @NotNull
    private static ImmutableCkbRegion createRegion(@NotNull String country, @NotNull String... states) {
        return ImmutableCkbRegion.builder().country(country).addStates(states).build();
    }
}