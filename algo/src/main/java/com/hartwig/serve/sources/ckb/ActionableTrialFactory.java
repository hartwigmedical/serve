package com.hartwig.serve.sources.ckb;

import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.jetbrains.annotations.NotNull;

class ActionableTrialFactory implements ActionableEntryFactory {

    private static final Set<String> POTENTIALLY_OPEN_RECRUITMENT_TYPES = Sets.newHashSet();
    private static final Set<String> COUNTRIES_TO_INCLUDE = Sets.newHashSet();
    private static final Set<String> VARIANT_REQUIREMENT_TYPES_TO_INCLUDE = Sets.newHashSet();

    static {
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("recruiting");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("active, not recruiting");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("unknown status");
        POTENTIALLY_OPEN_RECRUITMENT_TYPES.add("active_not_recruiting");

        COUNTRIES_TO_INCLUDE.add("netherlands");
        COUNTRIES_TO_INCLUDE.add("belgium");
        COUNTRIES_TO_INCLUDE.add("germany");

        VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.add("partial - required");
        VARIANT_REQUIREMENT_TYPES_TO_INCLUDE.add("required");
    }

    public ActionableTrialFactory() {
    }

    @NotNull
    @Override
    public Set<ActionableEntry> create(@NotNull CkbEntry entry, @NotNull String sourceEvent, @NotNull String sourceGene) {
        Set<ActionableEntry> actionableTrials = Sets.newHashSet();

        for (ClinicalTrial trial : trialsToInclude(entry)) {
            Set<String> countries = countriesToInclude(trial);

            if (!countries.isEmpty()) {
                for (Indication indication : trial.indications()) {
                    CancerTypeExtraction cancerTypeExtraction = ActionableFunctions.extractCancerTypeDetails(indication);

                    if (cancerTypeExtraction != null) {
                        actionableTrials.add(ImmutableActionableEntry.builder()
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
        }
        return actionableTrials;
    }

    @NotNull
    private static List<ClinicalTrial> trialsToInclude(@NotNull CkbEntry entry) {
        List<ClinicalTrial> filtered = Lists.newArrayList();
        for (ClinicalTrial trial : entry.clinicalTrials()) {
            if (hasVariantRequirementTypeToInclude(trial.variantRequirementDetails(), entry) && POTENTIALLY_OPEN_RECRUITMENT_TYPES.contains(
                    trial.recruitment().toLowerCase())) {
                filtered.add(trial);
            }
        }
        return filtered;
    }

    @NotNull
    @VisibleForTesting
    static Set<String> countriesToInclude(@NotNull ClinicalTrial trial) {
        Set<String> countries = Sets.newHashSet();
        for (Location location : trial.locations()) {
            if (COUNTRIES_TO_INCLUDE.contains(location.country().toLowerCase()) && (location.status() == null
                    || POTENTIALLY_OPEN_RECRUITMENT_TYPES.contains(location.status().toLowerCase()))) {
                countries.add(location.country());
            }
        }

        return countries;
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
}