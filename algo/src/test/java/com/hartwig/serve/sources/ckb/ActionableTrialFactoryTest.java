package com.hartwig.serve.sources.ckb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriteriumTestFactory;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.datamodel.trial.GenderCriterium;
import com.hartwig.serve.datamodel.trial.ImmutableCountry;
import com.hartwig.serve.datamodel.trial.ImmutableHospital;
import com.hartwig.serve.sources.ckb.filter.CkbFilteringTestFactory;
import com.hartwig.serve.sources.ckb.filter.CkbTrialFilterEntry;
import com.hartwig.serve.sources.ckb.filter.CkbTrialFilterModel;
import com.hartwig.serve.sources.ckb.filter.CkbTrialFilterType;
import com.hartwig.serve.sources.ckb.filter.ImmutableCkbTrialFilterEntry;
import com.hartwig.serve.sources.ckb.region.CkbRegion;
import com.hartwig.serve.sources.ckb.region.ImmutableCkbRegion;

import org.apache.commons.compress.utils.Sets;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class ActionableTrialFactoryTest {

    private static final CkbTrialFilterModel FILTER_MODEL = CkbFilteringTestFactory.createProperTrialFilterModel();
    private static final Set<CkbRegion> REGIONS_TO_INCLUDE =
            Set.of(createRegion("netherlands"), createRegion("belgium"), createRegion("germany"), createRegion("united states", "maine"));
    private static final Set<MolecularCriterium> TEST_MOLECULAR_CRITERIUM =
            Set.of(MolecularCriteriumTestFactory.createWithTestActionableGene());

    @Test
    public void canCreateActionableEntryForOpenTrialInAllowedCountryWithRequiredMolecularProfileAndValidAgeGroup() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting", "Rotterdam", "EMC");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial =
                CkbTestFactory.createTrialWithTherapy("NCT0102", "Phase I trial", List.of(CkbTestFactory.createTherapy("Nivolumab")),
                List.of(CkbTestFactory.createIndication("CUP", "JAX:10000006")),
                "Recruiting",
                List.of("senior", "child", "adult"),
                List.of(requirementDetail),
                List.of(location));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        ActionableTrialFactory factory = new ActionableTrialFactory(FILTER_MODEL, REGIONS_TO_INCLUDE);
        Set<ActionableTrial> trials = factory.create(entry, TEST_MOLECULAR_CRITERIUM, "KRAS", "gene");

        assertEquals(1, trials.size());
        ActionableTrial clinicalTrial1 = trials.iterator().next();

        assertEquals(Knowledgebase.CKB, clinicalTrial1.source());
        assertEquals("NCT0102", clinicalTrial1.nctId());
        assertEquals("Phase I trial", clinicalTrial1.title());
        assertNull(clinicalTrial1.acronym());
        assertEquals(Sets.newHashSet(ImmutableCountry.builder()
                .countryName("Netherlands")
                .hospitalsPerCity(Map.of("Rotterdam",
                        Sets.newHashSet(ImmutableHospital.builder().name("EMC").isChildrensHospital(false).build())))
                .build()), clinicalTrial1.countries());
        assertEquals(Sets.newHashSet("Nivolumab"), clinicalTrial1.therapyNames());
        assertEquals("CUP", clinicalTrial1.indications().iterator().next().applicableType().name());
        assertEquals("162", clinicalTrial1.indications().iterator().next().applicableType().doid());
        assertEquals(GenderCriterium.BOTH, clinicalTrial1.genderCriterium());
        assertEquals(TEST_MOLECULAR_CRITERIUM, clinicalTrial1.anyMolecularCriteria());
        assertEquals(Sets.newHashSet("https://clinicaltrials.gov/study/NCT0102"), clinicalTrial1.urls());
    }

    @Test
    public void canFilterTrials() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting", "Groningen", "UMCG");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial = CkbTestFactory.createTrialWithTherapy("NCT0456",
                "Phase I trial",
                List.of(CkbTestFactory.createTherapy("Nivolumab")),
                List.of(CkbTestFactory.createIndication("CUP", "JAX:10000006")),
                "Recruiting",
                List.of("senior", "child", "adult"),
                List.of(requirementDetail),
                List.of(location));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbTrialFilterModel model = createTrialFilterModel(CkbTrialFilterType.COMPLETE_TRIAL, "NCT0456", null, null, null, null);
        ActionableTrialFactory factory = new ActionableTrialFactory(model, REGIONS_TO_INCLUDE);
        Set<ActionableTrial> trials = factory.create(entry, TEST_MOLECULAR_CRITERIUM, "KRAS", "gene");
        assertEquals(0, trials.size());
    }

    @Test
    public void shouldNotFilterTrialsWithOtherNCT() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting", "Groningen", "UMCG");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial = CkbTestFactory.createTrialWithTherapy("NCT0456",
                "Phase I trial",
                List.of(CkbTestFactory.createTherapy("Nivolumab")),
                List.of(CkbTestFactory.createIndication("CUP", "JAX:10000006")),
                "Recruiting",
                List.of("senior", "child", "adult"),
                List.of(requirementDetail),
                List.of(location));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbTrialFilterModel model = createTrialFilterModel(CkbTrialFilterType.COMPLETE_TRIAL, "NCT123", null, null, null, null);
        ActionableTrialFactory factory = new ActionableTrialFactory(model, REGIONS_TO_INCLUDE);
        Set<ActionableTrial> trials = factory.create(entry, TEST_MOLECULAR_CRITERIUM, "KRAS", "gene");
        assertEquals(1, trials.size());
    }

    @Test
    public void canFilterTrialsOnGene() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting", "Groningen", "UMCG");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial = CkbTestFactory.createTrialWithTherapy("NCT0456",
                "Phase I trial",
                List.of(CkbTestFactory.createTherapy("Nivolumab")),
                List.of(CkbTestFactory.createIndication("CUP", "JAX:10000006")),
                "Recruiting",
                List.of("senior", "child", "adult"),
                List.of(requirementDetail),
                List.of(location));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbTrialFilterModel model = createTrialFilterModel(CkbTrialFilterType.ALL_TRIALS_BASED_ON_GENE, null, null, null, "EGFR", null);
        ActionableTrialFactory factory = new ActionableTrialFactory(model, REGIONS_TO_INCLUDE);
        Set<ActionableTrial> trials = factory.create(entry, TEST_MOLECULAR_CRITERIUM, "EGFR", "EGFR");
        assertEquals(0, trials.size());
    }

    @Test
    public void shouldNotFilterTrialOnOtherGene() {
        int profileId = 1;
        String profileName = Strings.EMPTY;
        Location location = CkbTestFactory.createLocation("Netherlands", "Recruiting", "Groningen", "UMCG");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(profileId, "required");
        ClinicalTrial clinicalTrial = CkbTestFactory.createTrialWithTherapy("NCT0456",
                "Phase I trial",
                List.of(CkbTestFactory.createTherapy("Nivolumab")),
                List.of(CkbTestFactory.createIndication("CUP", "JAX:10000006")),
                "Recruiting",
                List.of("senior", "child", "adult"),
                List.of(requirementDetail),
                List.of(location));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(profileId, profileName, clinicalTrial);

        CkbTrialFilterModel model = createTrialFilterModel(CkbTrialFilterType.ALL_TRIALS_BASED_ON_GENE, null, null, null, "ATM", null);
        ActionableTrialFactory factory = new ActionableTrialFactory(model, REGIONS_TO_INCLUDE);
        Set<ActionableTrial> trials = factory.create(entry, TEST_MOLECULAR_CRITERIUM, "EGFR", "EGFR");
        assertEquals(1, trials.size());
    }

    @Test
    public void shouldNotCreatTrialWhenVariantRequirementIsOnADifferentProfile() {
        Location location = CkbTestFactory.createLocation("Belgium", "Recruiting", "Brussel", "UZ Brussel");
        VariantRequirementDetail requirementDetail = CkbTestFactory.createVariantRequirementDetail(0, "required");
        ClinicalTrial clinicalTrial = CkbTestFactory.createTrial("NCT0102",
                "Phase I trial",
                "Recruiting",
                List.of("senior", "child", "adult"),
                List.of(requirementDetail),
                List.of(location));
        CkbEntry entry = CkbTestFactory.createEntryWithClinicalTrial(1, Strings.EMPTY, clinicalTrial);

        ActionableTrialFactory factory = new ActionableTrialFactory(FILTER_MODEL, new HashSet<>());
        Set<ActionableTrial> trials = factory.create(entry, TEST_MOLECULAR_CRITERIUM, "KRAS", "gene");

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

        ClinicalTrial trialPotentiallyOpenInBelgiumAndIndia = createTrialWithMultipleLocations("Recruiting",
                "Belgium",
                "Recruiting",
                "Brussel",
                null,
                "India",
                "Recruiting",
                "Mumbai",
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
                .hospitalsPerCity(Map.of("Brussel", Set.of(ImmutableHospital.builder().name("Unknown(Brussel)").build())))
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
                        .hospitalsPerCity(Map.of("Brussel", Set.of(ImmutableHospital.builder().name("UZ Brussel").build())))
                        .build(),
                ImmutableCountry.builder()
                        .countryName("Netherlands")
                        .hospitalsPerCity(Map.of("Rotterdam",
                                Set.of(ImmutableHospital.builder().name("EMC").isChildrensHospital(false).build())))
                        .build()), ActionableTrialFactory.extractCountriesToInclude(trialWithMultipleLocations, REGIONS_TO_INCLUDE));
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

    @Test
    public void canDetermineIfHospitalIsChildrensHospital() {
        assertTrue(ActionableTrialFactory.isChildrensHospital("PMC", "Netherlands"));
        assertFalse(ActionableTrialFactory.isChildrensHospital("UMCU", "Netherlands"));
        assertNull(ActionableTrialFactory.isChildrensHospital("UZ Brussel", "Belgium"));
    }

    @NotNull
    private static ClinicalTrial createTrialWithMultipleLocations(@NotNull String recruitmentTrial, @NotNull String country1,
            @NotNull String recruitmentCountry1, @NotNull String city1, @Nullable String facility1, @NotNull String country2,
            @NotNull String recruitmentCountry2, @NotNull String city2, @Nullable String facility2) {
        return CkbTestFactory.createTrial("nctid",
                "title",
                recruitmentTrial,
                List.of("senior", "child", "adult"),
                List.of(CkbTestFactory.createVariantRequirementDetail(0, "required")),
                List.of(CkbTestFactory.createLocation(country1, recruitmentCountry1, city1, facility1),
                        CkbTestFactory.createLocation(country2, recruitmentCountry2, city2, facility2)));
    }

    @NotNull
    private static ClinicalTrial createTrialWithOneLocation(@NotNull String recruitmentTrial, @NotNull String country, @NotNull String city,
            @NotNull String recruitmentCountry, @Nullable String facility) {
        return createTrialWithOneLocation(recruitmentTrial, country, city, recruitmentCountry, null, facility);
    }

    @NotNull
    private static ClinicalTrial createTrialWithOneLocation(@NotNull String recruitmentTrial, @NotNull String country, @NotNull String city,
            @NotNull String recruitmentCountry, @Nullable String state, @Nullable String facility) {
        return CkbTestFactory.createTrial("nctid",
                "title",
                recruitmentTrial,
                List.of("senior", "child", "adult"),
                List.of(CkbTestFactory.createVariantRequirementDetail(0, "required")),
                List.of(CkbTestFactory.createLocation(country, recruitmentCountry, city, state, facility)));
    }

    @NotNull
    public static CkbTrialFilterModel createTrialFilterModel(@NotNull CkbTrialFilterType type, @Nullable String nctId,
            @Nullable String therapy, @Nullable String cancerType, @Nullable String gene, @Nullable String event) {
        CkbTrialFilterEntry entry = ImmutableCkbTrialFilterEntry.builder()
                .type(type)
                .nctId(nctId)
                .therapy(therapy)
                .cancerType(cancerType)
                .gene(gene)
                .event(event)
                .build();

        return CkbFilteringTestFactory.createSpecificTrialFilterModel(entry);
    }

    @NotNull
    private static ImmutableCkbRegion createRegion(@NotNull String country, @NotNull String... states) {
        return ImmutableCkbRegion.builder().country(country).addStates(states).build();
    }
}