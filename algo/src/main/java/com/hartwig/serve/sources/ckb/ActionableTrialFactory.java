package com.hartwig.serve.sources.ckb;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.ckb.datamodel.therapy.Therapy;
import com.hartwig.serve.datamodel.ActionableTrial;
import com.hartwig.serve.datamodel.Country;
import com.hartwig.serve.datamodel.GenderCriterium;
import com.hartwig.serve.datamodel.Hospital;
import com.hartwig.serve.datamodel.ImmutableActionableTrial;
import com.hartwig.serve.datamodel.ImmutableCountry;
import com.hartwig.serve.datamodel.ImmutableHospital;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.sources.ckb.filter.CkbTrialFilterModel;
import com.hartwig.serve.datamodel.MolecularCriterium;
import com.hartwig.serve.sources.ckb.region.CkbRegion;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ActionableTrialFactory {

    private static final String SUB_FIELD_DELIMITER = ",";
    private static final Set<String> POTENTIALLY_OPEN_RECRUITMENT_TYPES = Set.of("recruiting",
            "not yet recruiting",
            "not_yet_recruiting",
            "approved for marketing",
            "available",
            "enrolling by invitation",
            "enrolling_by_invitation");
    private static final Set<String> VARIANT_REQUIREMENT_TYPES_TO_INCLUDE = Set.of("partial - required", "required");

    private static final Set<String> AGE_GROUPS_TO_INCLUDE = Set.of("adult", "senior");

    private static final Set<String> DUTCH_CHILDREN_HOSPITALS = Set.of("PMC",
            "WKZ",
            "EKZ",
            "JKZ",
            "BKZ",
            "WAKZ",
            "Sophia Kinderziekenhuis",
            "Amalia Kinderziekenhuis",
            "MosaKids Kinderziekenhuis");

    @NotNull
    private final CkbTrialFilterModel filterTrial;
    @NotNull
    private final Set<CkbRegion> regionsToInclude;

    public ActionableTrialFactory(@NotNull CkbTrialFilterModel filterTrial, @NotNull Set<CkbRegion> regionsToInclude) {
        this.filterTrial = filterTrial;
        this.regionsToInclude = regionsToInclude;
    }

    @NotNull
    public Set<ActionableTrial> create(@NotNull CkbEntry entry, @NotNull MolecularCriterium molecularCriterium, @NotNull String sourceEvent,
            @NotNull String sourceGene) {
        Set<ActionableTrial> actionableTrials = Sets.newHashSet();

        for (ClinicalTrial trial : trialsToInclude(entry)) {
            Set<Country> countries = extractCountriesToInclude(trial, regionsToInclude);
            Set<String> therapies = Sets.newHashSet();
            for (Therapy therapy : trial.therapies()) {
                therapies.add(therapy.therapyName());
            }

            // TODO (CB): This filtering was previously not done properly. Discussed with Lieke. If we want these complex filters we should have the clinical trial in the json multiple times, one for each combination of tumor type and therapy.
            if (!filterTrial.shouldFilterTrial(trial.nctId(), setToField(therapies), Strings.EMPTY, sourceGene, sourceEvent)
                    && !countries.isEmpty()) {
                List<Indication> filteredIndications = Lists.newArrayList();
                for (Indication indication : trial.indications()) {
                    if (!filterTrial.shouldFilterCancerType(trial.nctId(),
                            setToField(therapies),
                            indication.name(),
                            sourceGene,
                            sourceEvent)) {
                        filteredIndications.add(indication);
                    }
                }
                Set<com.hartwig.serve.datamodel.Indication> indications = getIndications(filteredIndications);
                actionableTrials.add(ImmutableActionableTrial.builder()
                        .source(Knowledgebase.CKB)
                        .nctId(trial.nctId())
                        .title(trial.title())
                        .acronym(trial.acronym())
                        .countries(countries)
                        .therapyNames(therapies)
                        .genderCriterium(trial.gender() != null ? GenderCriterium.valueOf(trial.gender()) : null)
                        .indications(indications)
                        // TODO (CB): trial can have multiple molecular criteria. One for each CkbEntry. Should be merged somewhere?
                        .anyMolecularCriteria(List.of(molecularCriterium))
                        .urls(Sets.newHashSet("https://clinicaltrials.gov/study/" + trial.nctId()))
                        .build());
            }
        }
        return actionableTrials;
    }

    @NotNull
    private static Set<com.hartwig.serve.datamodel.Indication> getIndications(@NotNull List<Indication> ckbIndications) {
        Set<com.hartwig.serve.datamodel.Indication> indications = new HashSet<>();
        for (Indication indication : ckbIndications) {
            com.hartwig.serve.datamodel.Indication cancerTypeExtraction = ActionableFunctions.extractIndication(indication);
            if (cancerTypeExtraction != null) {
                indications.add(cancerTypeExtraction);
            }
        }
        return indications;
    }

    @NotNull
    private static String setToField(@NotNull Set<String> strings) {
        StringJoiner joiner = new StringJoiner(SUB_FIELD_DELIMITER);
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }

    @NotNull
    private static List<ClinicalTrial> trialsToInclude(@NotNull CkbEntry entry) {
        List<ClinicalTrial> filtered = Lists.newArrayList();
        for (ClinicalTrial trial : entry.clinicalTrials()) {
            if (hasVariantRequirementTypeToInclude(trial.variantRequirementDetails(), entry)
                    && hasPotentiallyOpenRequirementToInclude(trial.recruitment()) && hasAgeGroupToInclude(trial.ageGroups())) {
                filtered.add(trial);
            }
        }
        return filtered;
    }

    @NotNull
    @VisibleForTesting
    static Set<Country> extractCountriesToInclude(@NotNull ClinicalTrial trial, @NotNull Set<CkbRegion> regionsToInclude) {
        Map<String, Map<String, Set<Hospital>>> countriesToCitiesToHospitalNames = trial.locations()
                .stream()
                .filter(location -> location.country() != null && location.city() != null)
                .filter(location -> regionsToInclude.stream().anyMatch(region -> region.includes(location))
                        && hasPotentiallyOpenRequirementToInclude(location.status()))
                .collect(Collectors.groupingBy(Location::country,
                        Collectors.groupingBy(Location::city,
                                Collectors.mapping((Location location) -> ImmutableHospital.builder()
                                        .name(location.facility() != null ? location.facility() : "")
                                        .isChildrensHospital(isChildrensHospital(location.facility(), location.country()))
                                        .build(), Collectors.toSet()))));

        return countriesToCitiesToHospitalNames.entrySet()
                .stream()
                .map(entry -> ImmutableCountry.builder().countryName(entry.getKey()).hospitalsPerCity(entry.getValue()).build())
                .collect(Collectors.toSet());
    }

    @VisibleForTesting
    static Boolean isChildrensHospital(@Nullable String hospitalName, @NotNull String country) {
        if (country.equals("Netherlands")) {
            return DUTCH_CHILDREN_HOSPITALS.contains(hospitalName);
        } else {
            return null;
        }
    }

    @VisibleForTesting
    static boolean hasVariantRequirementTypeToInclude(@NotNull List<VariantRequirementDetail> variantRequirementDetails,
            @NotNull CkbEntry entry) {
        for (VariantRequirementDetail variantRequirementDetail : variantRequirementDetails) {
            // Check if trial should be included based on the molecular profile of the current entry (trial can be linked to multiple molecular profiles)
            if (entry.profileId() == variantRequirementDetail.profileId() && VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.contains(
                    variantRequirementDetail.requirementType().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    static boolean hasAgeGroupToInclude(@NotNull List<String> ageGroups) {
        for (String age : ageGroups) {
            if (AGE_GROUPS_TO_INCLUDE.contains(age.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    static boolean hasPotentiallyOpenRequirementToInclude(@Nullable String recruitmentStatus) {
        return recruitmentStatus == null || POTENTIALLY_OPEN_RECRUITMENT_TYPES.contains(recruitmentStatus.toLowerCase());
    }
}