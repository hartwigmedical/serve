package com.hartwig.serve.sources.ckbtrial;

import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.cancertype.CancerTypeConstants;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.datamodel.CancerType;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableCancerType;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ActionableTrialFactory {

    private static final Logger LOGGER = LogManager.getLogger(ActionableTrialFactory.class);
    private static final Set<String> POTENTIALLY_OPEN_RECRUITMENT_TYPES = Sets.newHashSet();
    private static final Set<String> COUNTRIES_TO_INCLUDE = Sets.newHashSet();
    private static final Set<String> VARIANT_REQUIREMENT_TYPES_TO_INCLUDE = Sets.newHashSet();

    static {
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("Recruiting");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("Active, not recruiting");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("Unknown status");

        COUNTRIES_TO_INCLUDE.add("Netherlands");
        COUNTRIES_TO_INCLUDE.add("Belgium");
        COUNTRIES_TO_INCLUDE.add("Germany");

        VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.add("partial - required");
        VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.add("required");
    }

    ActionableTrialFactory() {
    }

    @NotNull
    public static Set<ActionableTrial> toActionableTrials(@NotNull CkbEntry entry, @NotNull String sourceEvent) {
        Set<ActionableTrial> actionableTrials = Sets.newHashSet();

        for (ClinicalTrial trial : trialsToInclude(entry.clinicalTrials(), entry)) {
            for (Indication indication : trial.indications()) {
                CancerTypeExtraction cancerTypeExtraction = extractCancerTypeDetails(indication);

                if (cancerTypeExtraction != null) {
                    Set<String> countries = trialsWithCountriesToInclude(trial);

                    actionableTrials.add(ImmutableActionableTrial.builder()
                            .source(Knowledgebase.CKB_TRIAL)
                            .sourceEvent(sourceEvent)
                            .sourceUrls(Sets.newHashSet("https://clinicaltrials.gov/study/" + trial.nctId()))
                            .treatment(ImmutableTreatment.builder().name(trial.nctId()).build())
                            .applicableCancerType(cancerTypeExtraction.applicableCancerType())
                            .blacklistCancerTypes(cancerTypeExtraction.blacklistedCancerTypes())
                            .level(EvidenceLevel.B)
                            .direction(EvidenceDirection.RESPONSIVE)
                            .evidenceUrls(countries)
                            .build());
                }
            }
        }
        return actionableTrials;
    }

    @Nullable
    @VisibleForTesting
    static String[] extractSourceCancerTypeId(@Nullable String doidString) {
        if (doidString == null) {
            return null;
        }

        String[] parts = doidString.split(":");
        if (parts.length == 2) {
            String source = parts[0];
            if (source.equalsIgnoreCase("doid") || source.equalsIgnoreCase("jax")) {
                return parts;
            } else {
                LOGGER.warn("Unexpected length of doid string '{}'", doidString);
                return null;
            }
        } else {
            LOGGER.warn("Unexpected DOID string in CKB: '{}'", doidString);
            return null;
        }
    }

    @Nullable
    @VisibleForTesting
    static CancerTypeExtraction extractCancerTypeDetails(@NotNull Indication indication) {
        String[] sourceCancerTypeDetails = extractSourceCancerTypeId(indication.termId());

        if (sourceCancerTypeDetails == null) {
            return null;
        }

        ImmutableCancerType.Builder applicableCancerTypeBuilder = ImmutableCancerType.builder().name(indication.name());
        Set<CancerType> blacklistedCancerTypes = Sets.newHashSet();

        assert sourceCancerTypeDetails.length == 2;
        String source = sourceCancerTypeDetails[0];
        String id = sourceCancerTypeDetails[1];
        if (source.equalsIgnoreCase("doid")) {
            applicableCancerTypeBuilder.doid(id);
        } else if (source.equalsIgnoreCase("jax")) {
            switch (id) {
                case CancerTypeConstants.JAX_ADVANCED_SOLID_TUMOR: {
                    applicableCancerTypeBuilder.doid(CancerTypeConstants.CANCER_DOID);
                    blacklistedCancerTypes.add(CancerTypeConstants.LEUKEMIA_TYPE);
                    blacklistedCancerTypes.add(CancerTypeConstants.REFRACTORY_HEMATOLOGIC_TYPE);
                    blacklistedCancerTypes.add(CancerTypeConstants.BONE_MARROW_TYPE);
                    break;
                }
                case CancerTypeConstants.JAX_CANCER_OF_UNKNOWN_PRIMARY: {
                    applicableCancerTypeBuilder.doid(CancerTypeConstants.CANCER_DOID);
                    break;
                }
                case CancerTypeConstants.JAX_CARCINOMA_OF_UNKNOWN_PRIMARY: {
                    applicableCancerTypeBuilder.doid(CancerTypeConstants.CARCINOMA_OF_UNKNOWN_PRIMARY);
                    break;
                }
                case CancerTypeConstants.JAX_ADENOCARCINOMA_OF_UNKNOWN_PRIMARY: {
                    applicableCancerTypeBuilder.doid(CancerTypeConstants.ADENOCARCINOMA_OF_UNKNOWN_PRIMARY);
                    break;
                }
                case CancerTypeConstants.JAX_SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY: {
                    applicableCancerTypeBuilder.doid(CancerTypeConstants.SQUAMOUS_CELL_CARCINOMA_OF_UNKNOWN_PRIMARY);
                    break;
                }
                default: {
                    // CKB uses 10000005 for configuring "Not a cancer". We can ignore these.
                    if (!id.equals(CancerTypeConstants.JAX_NOT_CANCER)) {
                        LOGGER.warn("Unexpected DOID string annotated by CKB: '{}'", source + ":" + id);
                    }
                    return null;
                }
            }
        } else {
            LOGGER.warn("Unexpected source '{}'", source);
            return null;
        }

        return new CancerTypeExtraction(applicableCancerTypeBuilder.build(), blacklistedCancerTypes);
    }

    @NotNull
    private static List<ClinicalTrial> trialsToInclude(@NotNull List<ClinicalTrial> trials,
            CkbEntry entry) {
        List<ClinicalTrial> filtered = Lists.newArrayList();
        for (ClinicalTrial trial : trials) {
            if (hasCountryToIncludeWithPotentiallyOpenRecruitmentType(trial.locations())
                    && hasVariantRequirementTypeToInclude(trial.variantRequirementDetails(), entry)) {
                filtered.add(trial);
            }
        }
        return filtered;
    }

    @VisibleForTesting
    static boolean hasCountryToIncludeWithPotentiallyOpenRecruitmentType(@NotNull List<Location> locations) {
        for (Location location : locations) {
            if (COUNTRIES_TO_INCLUDE.contains(location.country()) && (POTENTIALLY_OPEN_RECRUITMENT_TYPES.contains(location.status()) || location.status() == null)) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    static boolean hasVariantRequirementTypeToInclude(@NotNull List<VariantRequirementDetail> variantRequirementDetails, CkbEntry entry) {
        for (VariantRequirementDetail variantRequirementDetail : variantRequirementDetails) {
            if (entry.profileId() == variantRequirementDetail.profileId() && VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.contains(
                    variantRequirementDetail.requirementType())) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    private static Set<String> trialsWithCountriesToInclude(ClinicalTrial trial) {
        Set<String> countries = Sets.newHashSet();
        for (Location location : trial.locations()) {
            if (COUNTRIES_TO_INCLUDE.contains(location.country()) && POTENTIALLY_OPEN_RECRUITMENT_TYPES.contains(trial.recruitment()) && (POTENTIALLY_OPEN_RECRUITMENT_TYPES.contains(location.status()) || location.status() == null)) {
                countries.add(location.country());
            }
        }
        return countries;
    }

    static class CancerTypeExtraction {

        @NotNull
        private final CancerType applicableCancerType;
        @NotNull
        private final Set<CancerType> blacklistedCancerTypes;

        public CancerTypeExtraction(@NotNull CancerType applicableCancerType, @NotNull Set<CancerType> blacklistedCancerTypes) {
            this.applicableCancerType = applicableCancerType;
            this.blacklistedCancerTypes = blacklistedCancerTypes;
        }

        @NotNull
        public CancerType applicableCancerType() {
            return applicableCancerType;
        }

        @NotNull
        public Set<CancerType> blacklistedCancerTypes() {
            return blacklistedCancerTypes;
        }
    }
}