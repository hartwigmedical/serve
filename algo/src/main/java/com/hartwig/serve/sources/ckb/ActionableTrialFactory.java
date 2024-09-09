package com.hartwig.serve.sources.ckb;

import java.time.LocalDate;
import java.util.Collections;
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
import com.hartwig.serve.datamodel.EvidenceLevelDetails;
import com.hartwig.serve.datamodel.Country;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableClinicalTrial;
import com.hartwig.serve.datamodel.ImmutableCountry;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.sources.ckb.blacklist.CkbStudyBlacklistModel;
import com.hartwig.serve.sources.ckb.region.CkbRegion;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ActionableTrialFactory implements ActionableEntryFactory {

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

    @NotNull
    private final CkbStudyBlacklistModel blacklistStudy;
    @NotNull
    private final Set<CkbRegion> regionsToInclude;

    public ActionableTrialFactory(@NotNull CkbStudyBlacklistModel blacklistStudy, @NotNull Set<CkbRegion> regionsToInclude) {
        this.blacklistStudy = blacklistStudy;
        this.regionsToInclude = regionsToInclude;
    }

    @NotNull
    @Override
    public Set<ActionableEntry> create(@NotNull CkbEntry entry, @NotNull String sourceEvent, @NotNull String sourceGene) {
        Set<ActionableEntry> actionableTrials = Sets.newHashSet();

        for (ClinicalTrial trial : trialsToInclude(entry)) {
            Set<Country> countries = extractCountriesToInclude(trial, regionsToInclude);

            if (!countries.isEmpty()) {
                Set<String> therapies = Sets.newHashSet();
                for (Therapy therapy : trial.therapies()) {
                    therapies.add(therapy.therapyName());
                }
                for (Indication indication : trial.indications()) {
                    CancerTypeExtraction cancerTypeExtraction = ActionableFunctions.extractCancerTypeDetails(indication);
                    if (cancerTypeExtraction != null) {
                        if (!blacklistStudy.isBlacklistStudy(trial.nctId(),
                                setToField(therapies),
                                cancerTypeExtraction.applicableCancerType().name(),
                                sourceGene,
                                sourceEvent)) {

                            Set<String> sourceUrls = Collections.singleton(
                                    "https://ckbhome.jax.org/profileResponse/advancedEvidenceFind?molecularProfileId=" + entry.profileId());

                            actionableTrials.add(ImmutableActionableEntry.builder()
                                    .source(Knowledgebase.CKB_TRIAL)
                                    .sourceEvent(sourceEvent)
                                    .sourceUrls(sourceUrls)
                                    .intervention(ImmutableClinicalTrial.builder()
                                            .nctId(trial.nctId())
                                            .title(trial.title())
                                            .acronym(trial.acronym())
                                            .countries(countries)
                                            .therapyNames(therapies)
                                            .genderCriterium(trial.gender())
                                            .build())
                                    .applicableCancerType(cancerTypeExtraction.applicableCancerType())
                                    .blacklistCancerTypes(cancerTypeExtraction.blacklistedCancerTypes())
                                    .level(EvidenceLevel.B)
                                    .evidenceLevelDetails(EvidenceLevelDetails.CLINICAL_STUDY)
                                    .direction(EvidenceDirection.RESPONSIVE)
                                    .evidenceUrls(Sets.newHashSet("https://clinicaltrials.gov/study/" + trial.nctId()))
                                    .date(LocalDate.EPOCH)
                                    .description(Strings.EMPTY)
                                    .build());
                        }
                    }
                }
            }
        }

        return actionableTrials;
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
        Map<String, Map<String, Set<String>>> countriesToCitiesToHospitalNames = trial.locations()
                .stream()
                .filter(location -> regionsToInclude.stream().anyMatch(region -> region.includes(location))
                        && hasPotentiallyOpenRequirementToInclude(location.status()))
                .collect(Collectors.groupingBy(Location::country,
                        Collectors.groupingBy(Location::city, Collectors.mapping(Location::facility, Collectors.toSet()))));

        return countriesToCitiesToHospitalNames.entrySet()
                .stream()
                .map(entry -> ImmutableCountry.builder().countryName(entry.getKey()).hospitalsPerCity(entry.getValue()).build())
                .collect(Collectors.toSet());
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